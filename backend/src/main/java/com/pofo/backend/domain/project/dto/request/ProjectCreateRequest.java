package com.pofo.backend.domain.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ProjectCreateRequest {

    @NotBlank
    private String name;
    @NotBlank
    private Date startDate;
    @NotBlank
    private Date endDate;
    @NotBlank
    private int memberCount;
    @NotBlank
    private String position;

    private String repositoryLink;

    @NotBlank
    private String description;
    @NotBlank
    private String imageUrl;
}
