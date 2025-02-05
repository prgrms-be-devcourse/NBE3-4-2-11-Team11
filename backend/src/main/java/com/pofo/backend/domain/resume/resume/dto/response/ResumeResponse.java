package com.pofo.backend.domain.resume.resume.dto.response;

import com.pofo.backend.domain.resume.activity.activity.dto.ActivityResponse;
import com.pofo.backend.domain.resume.course.dto.CourseResponse;
import com.pofo.backend.domain.resume.education.dto.EducationResponse;
import com.pofo.backend.domain.resume.experience.dto.ExperienceResponse;
import com.pofo.backend.domain.resume.language.dto.LanguageResponse;
import com.pofo.backend.domain.resume.license.dto.LicenseResponse;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ResumeResponse {
    private String name;
    private LocalDate birth;
    private String number;
    private String email;
    private String address;
    private String gitAddress;
    private String blogAddress;
    private List<ActivityResponse> activities;
    private List<CourseResponse> courses;
    private List<ExperienceResponse> experiences;
    private List<EducationResponse> educations;
    private List<LicenseResponse> licenses;
    private List<LanguageResponse> languages;
}
