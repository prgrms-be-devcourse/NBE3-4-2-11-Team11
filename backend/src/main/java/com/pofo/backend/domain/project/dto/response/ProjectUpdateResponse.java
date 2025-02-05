package com.pofo.backend.domain.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class ProjectUpdateResponse {

    private Long projectId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    private int memberCount;
    private String position;
    private String repositoryLink;
    private String description;
    private String imageUrl;
}
