package com.pofo.backend.domain.project.service;

import com.pofo.backend.domain.mapper.ProjectMapper;
import com.pofo.backend.domain.project.dto.request.ProjectCreateRequest;
import com.pofo.backend.domain.project.dto.request.ProjectUpdateRequest;
import com.pofo.backend.domain.project.dto.response.ProjectCreateResponse;
import com.pofo.backend.domain.project.dto.response.ProjectDetailResponse;
import com.pofo.backend.domain.project.dto.response.ProjectUpdateResponse;
import com.pofo.backend.domain.project.entity.Project;
import com.pofo.backend.domain.project.exception.ProjectCreationException;
import com.pofo.backend.domain.project.repository.ProjectRepository;
import com.pofo.backend.domain.skill.entity.ProjectSkill;
import com.pofo.backend.domain.skill.service.SkillService;
import com.pofo.backend.domain.tool.entity.ProjectTool;
import com.pofo.backend.domain.tool.service.ToolService;
import com.pofo.backend.domain.user.join.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final SkillService skillService;
    private final ToolService toolService;

    public ProjectCreateResponse createProject(ProjectCreateRequest projectRequest, User user) {

        try{

            System.out.println(user.getId());

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
                    .projectSkills(projectRequest.getSkillNames().stream()
                            .map(skillName -> new ProjectSkill(null, skillService.getSkillByName(skillName)))
                            .collect(Collectors.toList()))
                    .projectTools(projectRequest.getToolNames().stream()
                            .map(toolName -> new ProjectTool(null, toolService.getToolByName(toolName)))
                            .collect(Collectors.toList()))
                    .build();

            projectRepository.save(project);

            return new ProjectCreateResponse(project.getId());

        }catch (Exception ex){
            throw ProjectCreationException.badRequest("프로젝트 등록 중 오류가 발생했습니다.");
        }
    }

    public List<ProjectDetailResponse> detailAllProject(User user){

        try{
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

        }catch (DataAccessException ex){
            throw ProjectCreationException.serverError("프로젝트 전체 조회 중 데이터베이스 오류가 발생했습니다.");
        }catch (ProjectCreationException ex) {
            throw ex;  // 이미 정의된 예외는 다시 던진다.
        }catch (Exception ex){
            throw ProjectCreationException.badRequest("프로젝트 전체 조회 중 오류가 발생했습니다.");
        }
    }

    public ProjectDetailResponse detailProject(Long projectId, User user) {

        try{
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> ProjectCreationException.notFound("해당 프로젝트를 찾을 수 없습니다."));

            if(!project.getUser().equals(user)){
                throw ProjectCreationException.forbidden("프로젝트 단건 조회 할 권한이 없습니다.");
            }

            return projectMapper.projectToProjectDetailResponse(project);

        }catch (DataAccessException ex){
            throw ProjectCreationException.serverError("프로젝트 단건 조회 중 데이터베이스 오류가 발생했습니다.");
        }catch (ProjectCreationException ex) {
            throw ex;  // 이미 정의된 예외는 다시 던진다.
        }catch (Exception ex){
            throw ProjectCreationException.badRequest("프로젝트 단건 조회 중 오류가 발생했습니다.");
        }
    }

    public ProjectUpdateResponse updateProject(Long projectId, ProjectUpdateRequest request, User user) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> ProjectCreationException.notFound("해당 프로젝트를 찾을 수 없습니다."));

        try {

            if(!project.getUser().equals(user)){
                throw ProjectCreationException.forbidden("프로젝트 수정 할 권한이 없습니다.");
            }


            // 프로젝트 정보 업데이트
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

            // 새로운 스킬 및 툴 리스트 생성
            updateProjectSkills(project, request.getSkillNames());
            updateProjectTools(project, request.getToolNames());

            // 응답 변환
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
                    project.getProjectSkills().stream().map(ps -> ps.getSkill().getName()).collect(Collectors.toList()),
                    project.getProjectTools().stream().map(pt -> pt.getTool().getName()).collect(Collectors.toList())
            );

        }catch (DataAccessException ex){
            throw ProjectCreationException.serverError("프로젝트 수정 중 데이터베이스 오류가 발생했습니다.");
        }catch (ProjectCreationException ex) {
            throw ex;  // 이미 정의된 예외는 다시 던진다.
        }catch (Exception ex){
            ex.printStackTrace(); // 예외 상세 출력
            throw ProjectCreationException.badRequest("프로젝트 수정 중 오류가 발생했습니다.");
        }

    }

    private void updateProjectSkills(Project project, List<String> skillNames) {
        // 현재 프로젝트에 연결된 Skill 가져오기
        Map<String, ProjectSkill> existingSkills = project.getProjectSkills().stream()
                .collect(Collectors.toMap(ps -> ps.getSkill().getName(), ps -> ps));

        // 새로운 Skill 목록 생성
        List<ProjectSkill> updatedSkills = skillNames.stream()
                .map(skillName -> existingSkills.getOrDefault(skillName,
                        new ProjectSkill(project, skillService.getSkillByName(skillName))))
                .collect(Collectors.toList());

        project.getProjectSkills().clear();
        project.getProjectSkills().addAll(updatedSkills);
    }

    private void updateProjectTools(Project project, List<String> toolNames) {
        // 현재 프로젝트에 연결된 Tool 가져오기
        Map<String, ProjectTool> existingTools = project.getProjectTools().stream()
                .collect(Collectors.toMap(pt -> pt.getTool().getName(), pt -> pt));

        // 새로운 Tool 목록 생성
        List<ProjectTool> updatedTools = toolNames.stream()
                .map(toolName -> existingTools.getOrDefault(toolName,
                        new ProjectTool(project, toolService.getToolByName(toolName))))
                .collect(Collectors.toList());

        project.getProjectTools().clear();
        project.getProjectTools().addAll(updatedTools);
    }

    public void deleteProject(Long projectId, User user) {

        try {

            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> ProjectCreationException.notFound("해당 프로젝트를 찾을 수 없습니다."));

            if (!project.getUser().equals(user)) {
                throw ProjectCreationException.forbidden("프로젝트 삭제 할 권한이 없습니다.");
            }

            projectRepository.delete(project);

        } catch (DataAccessException ex) {
            throw ProjectCreationException.serverError("프로젝트 삭제 중 데이터베이스 오류가 발생했습니다.");
        } catch (ProjectCreationException ex) {
            throw ex;  // 이미 정의된 예외는 다시 던진다.
        } catch (Exception ex) {
            throw ProjectCreationException.badRequest("프로젝트 삭제 중 오류가 발생했습니다.");
        }

    }

}
