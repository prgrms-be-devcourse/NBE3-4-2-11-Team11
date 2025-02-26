package com.pofo.backend.domain.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class ProjectDetailResponse {

    private Long projectId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    private int memberCount;
    private String position;
    private String repositoryLink;
    private String description;
    private String imageUrl;

    // 기술 및 도구 목록 추가
    private List<String> skills;
    private List<String> tools;

    private boolean isDeleted;
}
