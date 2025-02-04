package com.pofo.backend.domain.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
public class ProjectDetailResponse {

    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    private int memberCount;
    private String position;
    private String repositoryLink;
    private String description;
    private String imageUrl;
}
