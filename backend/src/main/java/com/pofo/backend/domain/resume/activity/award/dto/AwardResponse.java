package com.pofo.backend.domain.resume.activity.award.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AwardResponse {
    private Long id;
    private String name;
    private String institution;
    private String awardDate;
}