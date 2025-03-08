package com.pofo.backend.domain.mapper;

import com.pofo.backend.domain.project.dto.response.ProjectDetailResponse;
import com.pofo.backend.domain.project.entity.Project;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-04T15:42:56+0900",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 21.0.5 (Oracle Corporation)"
)
@Component
public class ProjectMapperImpl implements ProjectMapper {

    @Override
    public ProjectDetailResponse projectToProjectDetailResponse(Project project) {
        if ( project == null ) {
            return null;
        }

        ProjectDetailResponse.ProjectDetailResponseBuilder projectDetailResponse = ProjectDetailResponse.builder();

        projectDetailResponse.projectId( project.getId() );
        projectDetailResponse.name( project.getName() );
        projectDetailResponse.startDate( project.getStartDate() );
        projectDetailResponse.endDate( project.getEndDate() );
        projectDetailResponse.memberCount( project.getMemberCount() );
        projectDetailResponse.position( project.getPosition() );
        projectDetailResponse.repositoryLink( project.getRepositoryLink() );
        projectDetailResponse.description( project.getDescription() );
        projectDetailResponse.imageUrl( project.getImageUrl() );
        projectDetailResponse.thumbnailPath( project.getThumbnailPath() );

        projectDetailResponse.skills( project.getProjectSkills().stream().map(ps -> ps.getSkill().getName()).collect(java.util.stream.Collectors.toList()) );
        projectDetailResponse.tools( project.getProjectTools().stream().map(pt -> pt.getTool().getName()).collect(java.util.stream.Collectors.toList()) );

        return projectDetailResponse.build();
    }
}
