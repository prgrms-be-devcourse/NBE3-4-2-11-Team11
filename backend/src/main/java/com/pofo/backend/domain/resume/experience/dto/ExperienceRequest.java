package com.pofo.backend.domain.resume.experience.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExperienceRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String department;
    @NotBlank
    private String position;
    @NotBlank
    private String responsibility;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
}