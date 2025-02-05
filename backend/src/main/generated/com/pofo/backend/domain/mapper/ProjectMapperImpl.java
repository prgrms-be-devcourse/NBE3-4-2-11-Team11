package com.pofo.backend.domain.mapper;

import com.pofo.backend.domain.project.dto.response.ProjectDetailResponse;
import com.pofo.backend.domain.project.entity.Project;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-05T10:11:44+0900",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 21.0.2 (GraalVM Community)"
)
@Component
public class ProjectMapperImpl implements ProjectMapper {

    @Override
    public ProjectDetailResponse projectToProjectDetailResponse(Project project) {
        if ( project == null ) {
            return null;
        }

        String name = null;
        LocalDate startDate = null;
        LocalDate endDate = null;
        int memberCount = 0;
        String position = null;
        String repositoryLink = null;
        String description = null;
        String imageUrl = null;

        name = project.getName();
        startDate = project.getStartDate();
        endDate = project.getEndDate();
        memberCount = project.getMemberCount();
        position = project.getPosition();
        repositoryLink = project.getRepositoryLink();
        description = project.getDescription();
        imageUrl = project.getImageUrl();

        ProjectDetailResponse projectDetailResponse = new ProjectDetailResponse( name, startDate, endDate, memberCount, position, repositoryLink, description, imageUrl );

        return projectDetailResponse;
    }
}
