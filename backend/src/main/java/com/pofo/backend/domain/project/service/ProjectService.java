package com.pofo.backend.domain.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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


    public ProjectCreateResponse createProject(String projectRequestJson, User user, MultipartFile thumbnail) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // LocalDate 변환 지원
        ProjectCreateRequest projectRequest;

        try {
            if (projectRequestJson == null) {
                throw ProjectCreationException.badRequest("projectRequest가 필요합니다.");
            }
            projectRequest = objectMapper.readValue(projectRequestJson, ProjectCreateRequest.class);
        } catch (JsonProcessingException e) {
            throw ProjectCreationException.badRequest("잘못된 JSON 형식입니다.");
        }

        try {
            String thumbnailPath = null;
            if (thumbnail != null && !thumbnail.isEmpty()) {
                thumbnailPath = fileService.uploadThumbnail(thumbnail);
            }

            Project project = Project.builder()
                    .user(user)
                    .name(projectRequest.getName())
                    .startDate(projectRequest.getStartDate())
                    .endDate(projectRequest.getEndDate())
                    .memberCount(projectRequest.getMemberCount())
                    .position(projectRequest.getPosition())
                    .repositoryLink(projectRequest.getRepositoryLink())
                    .description(projectRequest.getDescription())
                    .imageUrl(projectRequest.getImageUrl())
                    .thumbnailPath(thumbnailPath)
                    .isDeleted(false)
                    .build();

            projectRepository.save(project);

            skillService.addProjectSkills(project.getId(), projectRequest.getSkills());
            toolService.addProjectTools(project.getId(), projectRequest.getTools());

            return new ProjectCreateResponse(project.getId());

        }catch (ProjectCreationException ex) {
            throw ex;  // 이미 정의된 예외는 다시 던진다.
        }catch (Exception ex) {
            throw ProjectCreationException.serverError("프로젝트 등록 중 오류가 발생했습니다.");
        }
    }


    public List<ProjectDetailResponse> detailAllProject(User user) {

        try {
            List<Project> projects = projectRepository.findAllByOrderByIdDesc();

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
            List<Project> projects = projectRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);

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


    public ProjectUpdateResponse updateProject(Long projectId, String projectRequestJson, User user, MultipartFile thumbnail, Boolean deleteThumbnail) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // LocalDate 변환 지원
        ProjectUpdateRequest request = null;

        try {
            if (projectRequestJson != null && !projectRequestJson.trim().isEmpty()) {  // 빈 문자열 예외 처리 추가
                request = objectMapper.readValue(projectRequestJson, ProjectUpdateRequest.class);
            }
        } catch (JsonProcessingException | IllegalArgumentException e) {  // 예외 유형 확장
            throw ProjectCreationException.badRequest("잘못된 JSON 형식입니다.");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> ProjectCreationException.notFound("해당 프로젝트를 찾을 수 없습니다."));

        if (!project.getUser().equals(user)) {
            throw ProjectCreationException.forbidden("프로젝트 수정할 권한이 없습니다.");
        }

        try {
            String thumbnailPath = project.getThumbnailPath();

            // 썸네일 삭제 요청이 있을 경우 (null 체크 추가)
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

            // JSON이 포함된 경우만 업데이트 수행
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
            }

            // 썸네일이 수정되지 않은 경우 기존 썸네일 유지
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
                    request != null ? request.getSkills() : project.getProjectSkills().stream().map(ps -> ps.getSkill().getName()).collect(Collectors.toList()),
                    request != null ? request.getTools() : project.getProjectTools().stream().map(pt -> pt.getTool().getName()).collect(Collectors.toList()),
                    project.isDeleted()
            );

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

        List<Project> trashProjects = projectRepository.findAllByIdAndIsDeletedTrue(projectIds);

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
