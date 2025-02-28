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
import com.pofo.backend.domain.resume.resume.dto.response.SkillResponse;
import com.pofo.backend.domain.resume.resume.dto.response.ToolResponse;
import com.pofo.backend.domain.resume.resume.entity.Resume;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResumeMapper {
    @Mapping(target = "skills", expression = "java(mapSkills(resume))")
    @Mapping(target = "tools", expression = "java(mapTools(resume))")
    ResumeResponse resumeToResumeResponse(Resume resume);

    default Set<SkillResponse> mapSkills(Resume resume) {
        return resume.getResumeSkills().stream()
            .map(resumeSkill -> new SkillResponse(
                resumeSkill.getSkill().getId(),
                resumeSkill.getSkill().getName()
            ))
            .collect(Collectors.toSet());
    }

    default Set<ToolResponse> mapTools(Resume resume) {
        return resume.getResumeTools().stream()
            .map(resumeTool -> new ToolResponse(
                resumeTool.getTool().getId(),
                resumeTool.getTool().getName()
            ))
            .collect(Collectors.toSet());
    }
    ActivityResponse activityToActivityResponse(Activity activity);
    CourseResponse courseToCourseResponse(Course course);
    ExperienceResponse experienceToExperienceResponse(Experience experience);
    EducationResponse educationToEducationResponse(Education education);
    LicenseResponse licenseToLicenseResponse(License license);
    LanguageResponse languageToLanguageResponse(Language language);
}