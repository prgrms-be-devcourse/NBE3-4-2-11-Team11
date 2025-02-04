package com.pofo.backend.domain.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProjectCreateRequest {

    @NotBlank
    private String name;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    @NotNull
    private int memberCount;
    @NotBlank
    private String position;

    private String repositoryLink;

    @NotBlank
    private String description;
    @NotBlank
    private String imageUrl;

}
