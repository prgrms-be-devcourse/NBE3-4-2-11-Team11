package com.pofo.backend.domain.resume.activity.activity.dto;

import com.pofo.backend.domain.resume.activity.award.dto.AwardResponse;
import java.time.LocalDate;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ActivityResponse {
    private String name;
    private String history;
    private LocalDate startDate;
    private LocalDate endDate;
    private Set<AwardResponse> awards;
}