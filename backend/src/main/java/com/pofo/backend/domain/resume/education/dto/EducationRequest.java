package com.pofo.backend.domain.resume.education.dto;

import com.pofo.backend.domain.resume.education.entity.Education;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EducationRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String major;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    @NotBlank
    private String status; // 상태 (EXPECTED, GRADUATED, ENROLLED, REST)

    public Education.Status getStatusEnum() {
        return Education.Status.valueOf(status.toUpperCase());
    }
}