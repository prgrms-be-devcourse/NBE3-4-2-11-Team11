package com.pofo.backend.domain.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@AllArgsConstructor
@Getter
public class ProjectDetailResponse {

    private String name;
    private Date startDate;
    private Date endDate;

    private int memberCount;
    private String position;
    private String repositoryLink;
    private String description;
    private String imageUrl;
}
