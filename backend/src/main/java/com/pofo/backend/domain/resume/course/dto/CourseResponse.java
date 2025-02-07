package com.pofo.backend.domain.resume.course.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CourseResponse {
    private String name;
    private String institution;
    private LocalDate startDate;
    private LocalDate endDate;
}
