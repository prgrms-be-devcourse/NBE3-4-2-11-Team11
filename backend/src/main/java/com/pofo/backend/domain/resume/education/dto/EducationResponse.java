package com.pofo.backend.domain.resume.education.dto;

import com.pofo.backend.domain.resume.education.entity.Education;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EducationResponse {

    private String name;
    private String major;
    private LocalDate startDate;
    private LocalDate endDate;
    private Education.Status status;
}