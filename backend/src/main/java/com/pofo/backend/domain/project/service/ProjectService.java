package com.pofo.backend.domain.project.service;

import com.pofo.backend.domain.project.dto.request.ProjectCreateRequest;
import com.pofo.backend.domain.project.dto.response.ProjectCreateResponse;
import com.pofo.backend.domain.project.entity.Project;
import com.pofo.backend.domain.project.exception.ProjectCreationException;
import com.pofo.backend.domain.user.entity.User;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {


    public ProjectCreateResponse createProject(ProjectCreateRequest projectRequest, User user) {

        if(user==null){
            throw new ProjectCreationException("404","사용자가 존재하지 않습니다.");
        }
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
}
