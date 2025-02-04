package com.pofo.backend.domain.resume.activity.activity.dto;

import com.pofo.backend.domain.resume.activity.award.dto.AwardResponse;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ActivityResponse {
    private Long id;
    private String name;
    private String history;
    private String startDate;
    private String endDate;
    private List<AwardResponse> awards;
}