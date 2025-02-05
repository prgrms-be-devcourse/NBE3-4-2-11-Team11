package com.pofo.backend.domain.resume.experience.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExperienceResponse {
    private String name;
    private String department;
    private String position;
    private String responsibility;
    private LocalDate startDate;
    private LocalDate endDate;
}
