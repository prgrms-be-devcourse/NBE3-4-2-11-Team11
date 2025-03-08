package com.pofo.backend.domain.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pofo.backend.domain.mapper.ProjectMapper;
import com.pofo.backend.domain.project.dto.request.ProjectCreateRequest;
import com.pofo.backend.domain.project.dto.request.ProjectUpdateRequest;
import com.pofo.backend.domain.project.dto.response.ProjectCreateResponse;
import com.pofo.backend.domain.project.dto.response.ProjectDetailResponse;
import com.pofo.backend.domain.project.dto.response.ProjectUpdateResponse;
import com.pofo.backend.domain.project.entity.Project;
import com.pofo.backend.domain.project.exception.ProjectCreationException;
import com.pofo.backend.domain.project.repository.ProjectRepository;
import com.pofo.backend.domain.skill.repository.ProjectSkillRepository;
import com.pofo.backend.domain.skill.service.SkillService;
import com.pofo.backend.domain.tool.repository.ProjectToolRepository;
import com.pofo.backend.domain.tool.service.ToolService;
import com.pofo.backend.domain.user.join.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final SkillService skillService;
    private final ToolService toolService;

    private final ProjectSkillRepository projectSkillRepository;
    private final ProjectToolRepository projectToolRepository;

    private final FileService fileService;


    public ProjectCreateResponse createProject(User user, String projectRequestJson, MultipartFile thumbnail) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // LocalDate 변환 지원
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); //알 수 없는 필드 무시

        ProjectCreateRequest request;
        try {
            if (projectRequestJson == null) {
                throw ProjectCreationException.badRequest("projectRequest가 필요합니다.");
            }

            log.info("📢 [createProject] JSON 파싱 시작: {}", projectRequestJson);
            request = objectMapper.readValue(projectRequestJson, ProjectCreateRequest.class);
            log.info("✅ [createProject] JSON 파싱 완료: 프로젝트 이름 -> {}", request.getName());

        } catch (JsonProcessingException e) {
            throw ProjectCreationException.badRequest("잘못된 JSON 형식입니다.");
        }

        try {
            // ✅ 썸네일 저장
            String thumbnailPath = null;
            if (thumbnail != null && !thumbnail.isEmpty()) {
                thumbnailPath = fileService.uploadThumbnail(thumbnail);
                log.info("📢 [createProject] 썸네일 저장 완료: {}", thumbnailPath);
            }

            // ✅ 프로젝트 엔티티 생성 및 저장
            Project project = Project.builder()
                    .user(user)
                    .name(request.getName())
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .memberCount(request.getMemberCount())
                    .position(request.getPosition())
                    .repositoryLink(request.getRepositoryLink())
                    .description(request.getDescription())
                    .imageUrl(request.getImageUrl())
                    .thumbnailPath(thumbnailPath)
                    .isDeleted(false)
                    .build();

            projectRepository.save(project);

            // ✅ 기술 스택 & 사용 도구 저장
            skillService.addProjectSkills(project.getId(), request.getSkills());
            toolService.addProjectTools(project.getId(), request.getTools());

            return new ProjectCreateResponse(project.getId()); // ✅ projectId 반환

        } catch (ProjectCreationException ex) {
            throw ex;  // 이미 정의된 예외는 다시 던진다.
        }catch (Exception ex) {
            ex.printStackTrace();
            throw ProjectCreationException.serverError("프로젝트 등록 중 오류가 발생했습니다.");
        }
    }



    public List<ProjectDetailResponse> detailAllProject(User user) {

        try {
            List<Project> projects = projectRepository.findByIsDeletedFalseOrderByIdDesc();

            // 프로젝트가 없으면 예외 처리
            if (projects.isEmpty()) {
                throw ProjectCreationException.notFound("프로젝트가 존재하지 않습니다.");
            }

            // 사용자가 접근할 수 있는 프로젝트만 필터링 (본인 소유 또는 관리자)
            List<Project> accessibleProjects = projects.stream()
                    .filter(project -> project.getUser().equals(user))
                    .collect(Collectors.toList());

            // 사용자가 접근할 수 있는 프로젝트가 없으면 예외 발생
            if (accessibleProjects.isEmpty()) {
                throw ProjectCreationException.forbidden("프로젝트 전체 조회 할 권한이 없습니다.");
            }

            return accessibleProjects.stream()
                    .map(projectMapper::projectToProjectDetailResponse)
                    .collect(Collectors.toList());

        } catch (DataAccessException ex) {
            throw ProjectCreationException.serverError("프로젝트 전체 조회 중 데이터베이스 오류가 발생했습니다.");
        } catch (ProjectCreationException ex) {
            throw ex;  // 이미 정의된 예외는 다시 던진다.
        } catch (Exception ex) {
            throw ProjectCreationException.badRequest("프로젝트 전체 조회 중 오류가 발생했습니다.");
        }
    }

    public ProjectDetailResponse detailProject(Long projectId, User user) {

        try {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> ProjectCreationException.notFound("해당 프로젝트를 찾을 수 없습니다."));

            if (!project.getUser().equals(user)) {
                throw ProjectCreationException.forbidden("프로젝트 단건 조회 할 권한이 없습니다.");
            }

            return projectMapper.projectToProjectDetailResponse(project);

        } catch (DataAccessException ex) {
            throw ProjectCreationException.serverError("프로젝트 단건 조회 중 데이터베이스 오류가 발생했습니다.");
        } catch (ProjectCreationException ex) {
            throw ex;  // 이미 정의된 예외는 다시 던진다.
        } catch (Exception ex) {
            throw ProjectCreationException.badRequest("프로젝트 단건 조회 중 오류가 발생했습니다.");
        }
    }


    public List<ProjectDetailResponse> searchProjectsByKeyword(User user, String keyword) {
        try {
            // 이름이나 설명에 키워드가 포함된 프로젝트 검색
            List<Project> projects = projectRepository.searchByKeyword(keyword);

            // 접근 권한 필터링 (자신의 프로젝트만 조회)
            List<Project> accessibleProjects = projects.stream()
                    .filter(project -> project.getUser().equals(user))
                    .collect(Collectors.toList());

            if (accessibleProjects.isEmpty()) {
                throw ProjectCreationException.notFound("검색된 프로젝트가 없습니다.");
            }

            return accessibleProjects.stream()
                    .map(projectMapper::projectToProjectDetailResponse)
                    .collect(Collectors.toList());

        } catch (DataAccessException ex) {
            throw ProjectCreationException.serverError("프로젝트 검색 중 데이터베이스 오류가 발생했습니다.");
        } catch (ProjectCreationException ex) {
            throw ex;  // 정의된 예외 재전달
        } catch (Exception ex) {
            throw ProjectCreationException.badRequest("프로젝트 검색 중 오류가 발생했습니다.");
        }
    }


    @Transactional
    public ProjectUpdateResponse updateProject(
            Long projectId,
            String projectRequestJson,
            User user,
            MultipartFile thumbnail,
            Boolean deleteThumbnail
    ) {

        // JSON -> ProjectUpdateRequest 변환
        ProjectUpdateRequest request = null;
        try {
            if (projectRequestJson != null && !projectRequestJson.trim().isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule()); // LocalDate 변환 지원
                request = objectMapper.readValue(projectRequestJson, ProjectUpdateRequest.class);
            }
        } catch (JsonProcessingException e) {
            throw ProjectCreationException.badRequest("잘못된 JSON 형식입니다.");
        }

        // 프로젝트 조회
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> ProjectCreationException.notFound("해당 프로젝트를 찾을 수 없습니다."));

        // 권한 확인
        if (!project.getUser().equals(user)) {
            throw ProjectCreationException.forbidden("프로젝트 수정할 권한이 없습니다.");
        }

        // 프로젝트 데이터 업데이트 (썸네일 처리 포함)
        return updateProjectData(project, request, thumbnail, deleteThumbnail);
    }



    private ProjectUpdateResponse updateProjectData(
            Project project,
            ProjectUpdateRequest request,
            MultipartFile thumbnail,
            Boolean deleteThumbnail
    ) {
        try {
            String thumbnailPath = project.getThumbnailPath();

            // 썸네일 삭제 처리
            if (deleteThumbnail != null && deleteThumbnail) {
                if (thumbnailPath != null) {
                    fileService.deleteFile(thumbnailPath);
                }
                thumbnailPath = null;
            }

            // 새로운 썸네일 업로드
            if (thumbnail != null && !thumbnail.isEmpty()) {
                if (thumbnailPath != null) {
                    fileService.deleteFile(thumbnailPath);
                }
                thumbnailPath = fileService.uploadThumbnail(thumbnail);
            }

            // 기본 정보 업데이트
            if (request != null) {
                project.updateBasicInfo(
                        request.getName(),
                        request.getStartDate(),
                        request.getEndDate(),
                        request.getMemberCount(),
                        request.getPosition(),
                        request.getRepositoryLink(),
                        request.getDescription(),
                        request.getImageUrl()
                );

                if (request.getSkills() != null) {
                    skillService.updateProjectSkills(project.getId(), request.getSkills());
                }

                if (request.getTools() != null) {
                    toolService.updateProjectTools(project.getId(), request.getTools());
                }
            }

            project.setThumbnailPath(thumbnailPath != null ? thumbnailPath : project.getThumbnailPath());

            projectRepository.save(project);

            return new ProjectUpdateResponse(
                    project.getId(),
                    project.getName(),
                    project.getStartDate(),
                    project.getEndDate(),
                    project.getMemberCount(),
                    project.getPosition(),
                    project.getRepositoryLink(),
                    project.getDescription(),
                    project.getImageUrl(),
                    project.getThumbnailPath(),
                    skillService.getProjectSkillNames(project.getId()),
                    toolService.getProjectToolNames(project.getId()),
                    project.isDeleted()
            );

        } catch (ProjectCreationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw ProjectCreationException.serverError("프로젝트 수정 중 오류가 발생했습니다.");
        }
    }

    public void deleteProject(Long projectId, User user) {

        try {

            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> ProjectCreationException.notFound("해당 프로젝트를 찾을 수 없습니다."));

            if (!project.getUser().equals(user)) {
                throw ProjectCreationException.forbidden("프로젝트 삭제 할 권한이 없습니다.");
            }

            //중간 테이블 데이터 먼저 삭제
            project.setDeleted(true);
            projectRepository.save(project);

        } catch (DataAccessException ex) {
            throw ProjectCreationException.serverError("프로젝트 삭제 중 데이터베이스 오류가 발생했습니다.");
        } catch (ProjectCreationException ex) {
            throw ex;  // 이미 정의된 예외는 다시 던진다.
        } catch (Exception ex) {
            throw ProjectCreationException.badRequest("프로젝트 삭제 중 오류가 발생했습니다.");
        }

    }

    public void moveToTrash(List<Long> projectIds, User user) {
        try {
            List<Project> projects = projectRepository.findAllById(projectIds);

            for (Project project : projects) {
                if (!project.getUser().equals(user)) {
                    throw ProjectCreationException.forbidden("프로젝트 삭제 할 권한이 없습니다.");
                }
                project.setDeleted(true); // 휴지통 이동
            }

            projectRepository.saveAll(projects);  // 저장 시도 (이 부분에서 예외 발생 가능)

        } catch (DataAccessException ex) {
            throw ProjectCreationException.serverError("프로젝트 삭제 중 데이터베이스 오류가 발생했습니다.");
        } catch (ProjectCreationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw ProjectCreationException.badRequest("프로젝트 삭제 중 오류가 발생했습니다.");
        }
    }

    public List<ProjectDetailResponse> getDeletedProjects(User user) {
        List<Project> deletedProjects = projectRepository.findByUserAndIsDeletedTrue(user);

        return deletedProjects.stream()
                .map(projectMapper::projectToProjectDetailResponse)
                .collect(Collectors.toList());
    }

    //요청된 프로젝트 ID 중에서, 휴지통에 있는 프로젝트만 조회하고 검증하는 메서드
    private List<Project> validateTrashProjects(List<Long> projectIds) {

        List<Project> trashProjects = projectRepository.findByIdInAndIsDeletedTrue(projectIds);

        Set<Long> validTrashIds = trashProjects.stream()
                .map(Project::getId)
                .collect(Collectors.toSet());

        List<Long> invalidIds = projectIds.stream()
                .filter(id -> !validTrashIds.contains(id))
                .collect(Collectors.toList());

        if (!invalidIds.isEmpty()) {
            throw ProjectCreationException.badRequest(
                    "휴지통에 없는 프로젝트가 포함되어 있습니다: " + invalidIds
            );
        }

        return trashProjects;

    }

    public void restoreProjects(List<Long> projectIds, User user) {
        List<Project> trashProjects = validateTrashProjects(projectIds);

        trashProjects.forEach(project -> project.setDeleted(false));
        projectRepository.saveAll(trashProjects);
    }

    public void permanentlyDeleteProjects(List<Long> projectIds, User user) {
        List<Project> trashProjects = validateTrashProjects(projectIds);

        List<Long> userProjectIds = trashProjects.stream()
                .map(Project::getId)
                .collect(Collectors.toList());
        // 다중 삭제 한 번에 처리
        skillService.deleteProjectSkills(userProjectIds);
        toolService.deleteProjectTools(userProjectIds);
        projectRepository.deleteAll(trashProjects);
    }

}
