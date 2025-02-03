package com.pofo.backend.domain.project.dto.request;

import com.pofo.backend.domain.skill.entity.Skill;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ProjectCreateRequest {

    @NotBlank
    private String name;
    @NotNull
    private Date startDate;
    @NotNull
    private Date endDate;
    @NotNull
    private int memberCount;
    @NotBlank
    private String position;

    private String repositoryLink;

    @NotBlank
    private String description;
    @NotBlank
    private String imageUrl;

    private List<Skill> skills;
}
