package com.pofo.backend.domain.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

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

    private String thumbnailPath;

    // 프로젝트 상세 조회 응답에 기술 및 도구 목록 추가
    private List<String> skills;
    private List<String> tools;

    private boolean isDeleted;
}
