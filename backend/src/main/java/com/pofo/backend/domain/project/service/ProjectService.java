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
            throw new ProjectCreationException("400","프로젝트 등록 중 오류가 발생했습니다.");
        }
    }

    public List<ProjectDetailResponse> detailAllProject(User user){
        try{

            List<Project> projects = projectRepository.findAllByOrderByIdDesc();
            return projects.stream()
                    .map(projectMapper::projectToProjectDetailResponse)
                    .collect(Collectors.toList());

        }catch  (DataAccessException ex) {
            // 데이터베이스 관련 예외를 구체적으로 처리
            throw new ProjectCreationException("500", "프로젝트 조회 중 데이터베이스 오류가 발생했습니다.");
        } catch (Exception ex) {
            // 예기치 않은 예외 처리
            throw new ProjectCreationException("400", "프로젝트 전체 조회 중 오류가 발생했습니다.");
        }
    }
}
