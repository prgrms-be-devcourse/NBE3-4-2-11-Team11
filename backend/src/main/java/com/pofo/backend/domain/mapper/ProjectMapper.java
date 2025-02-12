package com.pofo.backend.domain.mapper;

import com.pofo.backend.domain.project.dto.response.ProjectDetailResponse;
import com.pofo.backend.domain.project.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    @Mapping(source = "id", target = "projectId")
    ProjectDetailResponse projectToProjectDetailResponse(Project project);
}