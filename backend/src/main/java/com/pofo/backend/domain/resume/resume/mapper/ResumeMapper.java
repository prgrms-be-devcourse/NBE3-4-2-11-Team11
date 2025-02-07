package com.pofo.backend.domain.resume.resume.mapper;

import com.pofo.backend.domain.resume.activity.activity.dto.ActivityResponse;
import com.pofo.backend.domain.resume.activity.activity.entity.Activity;
import com.pofo.backend.domain.resume.course.dto.CourseResponse;
import com.pofo.backend.domain.resume.course.entity.Course;
import com.pofo.backend.domain.resume.education.dto.EducationResponse;
import com.pofo.backend.domain.resume.education.entity.Education;
import com.pofo.backend.domain.resume.experience.dto.ExperienceResponse;
import com.pofo.backend.domain.resume.experience.entity.Experience;
import com.pofo.backend.domain.resume.language.dto.LanguageResponse;
import com.pofo.backend.domain.resume.language.entity.Language;
import com.pofo.backend.domain.resume.license.dto.LicenseResponse;
import com.pofo.backend.domain.resume.license.entity.License;
import com.pofo.backend.domain.resume.resume.dto.response.ResumeResponse;
import com.pofo.backend.domain.resume.resume.entity.Resume;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResumeMapper {
    ResumeResponse toResponse(Resume resume);
    ActivityResponse toResponse(Activity activity);
    CourseResponse toResponse(Course course);
    ExperienceResponse toResponse(Experience experience);
    EducationResponse toResponse(Education education);
    LicenseResponse toResponse(License license);
    LanguageResponse toResponse(Language language);
}