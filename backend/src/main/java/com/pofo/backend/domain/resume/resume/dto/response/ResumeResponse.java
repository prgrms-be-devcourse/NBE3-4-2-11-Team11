package com.pofo.backend.domain.resume.resume.dto.response;

import com.pofo.backend.domain.resume.activity.activity.dto.ActivityResponse;
import com.pofo.backend.domain.resume.course.dto.CourseResponse;
import com.pofo.backend.domain.resume.education.dto.EducationResponse;
import com.pofo.backend.domain.resume.experience.dto.ExperienceResponse;
import com.pofo.backend.domain.resume.language.dto.LanguageResponse;
import com.pofo.backend.domain.resume.license.dto.LicenseResponse;
import java.time.LocalDate;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ResumeResponse {
    private String name;
    private LocalDate birth;
    private String number;
    private String email;
    private String address;
    private String addressDetail;
    private String gitAddress;
    private String blogAddress;
    private Set<ActivityResponse> activities;
    private Set<CourseResponse> courses;
    private Set<ExperienceResponse> experiences;
    private Set<EducationResponse> educations;
    private Set<LicenseResponse> licenses;
    private Set<LanguageResponse> languages;
    private Set<SkillResponse> skills;
    private Set<ToolResponse> tools;
}

