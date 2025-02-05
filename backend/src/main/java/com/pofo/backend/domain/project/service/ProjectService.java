package com.pofo.backend.domain.project.service;

import com.pofo.backend.domain.mapper.ProjectMapper;
import com.pofo.backend.domain.project.dto.request.ProjectCreateRequest;
import com.pofo.backend.domain.project.dto.response.ProjectCreateResponse;
import com.pofo.backend.domain.project.dto.response.ProjectDetailResponse;
import com.pofo.backend.domain.project.entity.Project;
import com.pofo.backend.domain.project.exception.ProjectCreationException;
import com.pofo.backend.domain.project.repository.ProjectRepository;
import com.pofo.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public ProjectCreateResponse createProject(ProjectCreateRequest projectRequest, User user) {

        try{
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
                    .build();

            return new ProjectCreateResponse(project.getId());

        }catch (Exception ex){
            throw ProjectCreationException.invalidUser();
        }
    }

    public List<ProjectDetailResponse> detailAllProject(User user){

        if(user==null){
            throw ProjectCreationException.invalidUser();
        }
        try{
            List<Project> projects = projectRepository.findAllByOrderByIdDesc();

            // 프로젝트가 없으면 예외 처리
            if (projects.isEmpty()) {
                throw ProjectCreationException.notFound("프로젝트가 존재하지 않습니다.");
            }

            return projects.stream()
                    .map(projectMapper::projectToProjectDetailResponse)
                    .collect(Collectors.toList());

        }catch (DataAccessException ex){
            throw ProjectCreationException.serverError("프로젝트 조회 중 데이터베이스 오류가 발생했습니다.");
        }catch (ProjectCreationException ex) {
            throw ex;  // 이미 정의된 예외는 다시 던진다.
        }catch (Exception ex){
            throw ProjectCreationException.badRequest("프로젝트 전체 조회 중 오류가 발생했습니다.");
        }
    }

    public ProjectDetailResponse detailProject(Long projectId, User user) {

        if(user==null){
            throw ProjectCreationException.invalidUser();
        }

        try{
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> ProjectCreationException.notFound("해당 프로젝트를 찾을 수 없습니다."));

            return projectMapper.projectToProjectDetailResponse(project);

        }catch (DataAccessException ex){
            throw ProjectCreationException.serverError("프로젝트 조회 중 데이터베이스 오류가 발생했습니다.");
        }catch (ProjectCreationException ex) {
            throw ex;  // 이미 정의된 예외는 다시 던진다.
        }catch (Exception ex){
            throw ProjectCreationException.badRequest("프로젝트 단건 조회 중 오류가 발생했습니다.");
        }
    }
}
