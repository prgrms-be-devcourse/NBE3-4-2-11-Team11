package com.pofo.backend.domain.mapper;

import com.pofo.backend.domain.project.dto.response.ProjectDetailResponse;
import com.pofo.backend.domain.project.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    @Mapping(source = "id", target = "projectId")
    @Mapping(target = "skills", expression = "java(project.getProjectSkills().stream().map(ps -> ps.getSkill().getName()).collect(java.util.stream.Collectors.toList()))")
    @Mapping(target = "tools", expression = "java(project.getProjectTools().stream().map(pt -> pt.getTool().getName()).collect(java.util.stream.Collectors.toList()))")
    ProjectDetailResponse projectToProjectDetailResponse(Project project);
}