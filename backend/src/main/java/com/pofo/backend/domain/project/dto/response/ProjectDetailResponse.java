package com.pofo.backend.domain.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
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
    private String thumbnailPath;

    // 기술 및 도구 목록 추가
    @Builder.Default
    private List<String> skills = new ArrayList<>();

    @Builder.Default
    private List<String> tools= new ArrayList<>();

    private boolean isDeleted;
}
