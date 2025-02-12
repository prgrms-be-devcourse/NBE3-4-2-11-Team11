package com.pofo.backend.domain.resume.activity.award.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AwardResponse {
    private String name;
    private String institution;
    private LocalDate awardDate;
}