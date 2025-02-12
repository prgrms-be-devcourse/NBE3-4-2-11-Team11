package com.pofo.backend.domain.resume.activity.activity.dto;

import com.pofo.backend.domain.resume.activity.award.dto.AwardRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String history;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    private List<AwardRequest> awards = new ArrayList<>();

}