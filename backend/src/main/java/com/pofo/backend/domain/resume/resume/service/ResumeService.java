package com.pofo.backend.domain.resume.resume.service;

import com.pofo.backend.domain.resume.activity.activity.dto.ActivityResponse;
import com.pofo.backend.domain.resume.activity.activity.entity.Activity;
import com.pofo.backend.domain.resume.activity.activity.service.ActivityService;
import com.pofo.backend.domain.resume.activity.award.dto.AwardResponse;
import com.pofo.backend.domain.resume.course.dto.CourseResponse;
import com.pofo.backend.domain.resume.course.entity.Course;
import com.pofo.backend.domain.resume.course.service.CourseService;
import com.pofo.backend.domain.resume.education.dto.EducationResponse;
import com.pofo.backend.domain.resume.education.entity.Education;
import com.pofo.backend.domain.resume.education.service.EducationService;
import com.pofo.backend.domain.resume.experience.dto.ExperienceResponse;
import com.pofo.backend.domain.resume.experience.entity.Experience;
import com.pofo.backend.domain.resume.experience.service.ExperienceService;
import com.pofo.backend.domain.resume.language.dto.LanguageResponse;
import com.pofo.backend.domain.resume.language.entity.Language;
import com.pofo.backend.domain.resume.language.service.LanguageService;
import com.pofo.backend.domain.resume.license.dto.LicenseResponse;
import com.pofo.backend.domain.resume.license.entity.License;
import com.pofo.backend.domain.resume.license.service.LicenseService;
import com.pofo.backend.domain.resume.resume.dto.request.ResumeCreateRequest;
import com.pofo.backend.domain.resume.resume.dto.response.ResumeResponse;
import com.pofo.backend.domain.resume.resume.entity.Resume;
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException;
import com.pofo.backend.domain.resume.resume.exception.UnauthorizedActionException;
import com.pofo.backend.domain.resume.resume.repository.ResumeRepository;
import com.pofo.backend.domain.user.join.entity.User;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final ActivityService activityService;
    private final CourseService courseService;
    private final ExperienceService experienceService;
    private final EducationService educationService;
    private final LicenseService licenseService;
    private final LanguageService languageService;

    @Transactional
    public Resume createResume(ResumeCreateRequest request, User user) {
        Resume resume = buildResume(request, user);
        return saveResumeAndRelatedEntities(resume, request);
    }

    @Transactional
    public Resume updateResume(Long resumeId, ResumeCreateRequest request, User user) {
        Resume resume = findResumeByIdAndCheckOwnership(resumeId, user);
        updateResumeFields(resume, request);
        return saveResumeAndRelatedEntities(resume, request);
    }

    @Transactional
    public void deleteResume(Long resumeId, User user) {
        Resume resume = findResumeByIdAndCheckOwnership(resumeId, user);
        try {
            resumeRepository.delete(resume);
        } catch (DataAccessException e) {
            throw new ResumeCreationException("이력서 삭제 중 데이터베이스 오류가 발생했습니다.");
        }
    }

    @Transactional(readOnly = true)
    public Resume getResumeByUser(User user) {
        return resumeRepository.findResumeWithDetails(user)
            .orElseThrow(() -> new ResumeCreationException("이력서가 존재하지 않습니다."));
    }

    private Resume buildResume(ResumeCreateRequest request, User user) {
        return Resume.builder()
            .user(user)
            .name(request.getName())
            .birth(request.getBirth())
            .number(request.getNumber())
            .email(request.getEmail())
            .address(request.getAddress())
            .gitAddress(request.getGitAddress())
            .blogAddress(request.getBlogAddress())
            .build();
    }

    private Resume saveResumeAndRelatedEntities(Resume resume, ResumeCreateRequest request) {
        try {
            resume = resumeRepository.save(resume);
            addRelatedEntities(resume, request);
        } catch (DataAccessException e) {
            throw new ResumeCreationException("데이터베이스 오류가 발생했습니다.");
        }
        return resume;
    }

    private void addRelatedEntities(Resume resume, ResumeCreateRequest request) {
        if (request.getActivities() != null) {
            activityService.updateActivities(resume.getId(), request.getActivities());
        }
        if (request.getCourses() != null) {
            courseService.updateCourses(resume.getId(), request.getCourses());
        }
        if (request.getExperiences() != null) {
            experienceService.updateExperiences(resume.getId(), request.getExperiences());
        }
        if (request.getEducations() != null) {
            educationService.updateEducations(resume.getId(), request.getEducations());
        }
        if (request.getLicenses() != null) {
            licenseService.updateLicenses(resume.getId(), request.getLicenses());
        }
        if (request.getLanguages() != null) {
            languageService.updateLanguages(resume.getId(), request.getLanguages());
        }
    }

    private void updateResumeFields(Resume resume, ResumeCreateRequest request) {
        resume.toBuilder()
            .name(request.getName())
            .birth(request.getBirth())
            .number(request.getNumber())
            .email(request.getEmail())
            .address(request.getAddress())
            .gitAddress(request.getGitAddress())
            .blogAddress(request.getBlogAddress())
            .build();
    }

    private Resume findResumeByIdAndCheckOwnership(Long resumeId, User user) {
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new ResumeCreationException("이력서가 존재하지 않습니다."));
        if (!resume.getUser().equals(user)) {
            throw new UnauthorizedActionException("이력서를 수정할 권한이 없습니다.");
        }
        return resume;
    }

    public ResumeResponse getResumeResponse(User user) {
        Resume resume = resumeRepository.findByUser(user)
            .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다."));

        // 각 항목에 대한 변환 로직을 서비스에서 처리
        Set<ActivityResponse> activities = convertActivities(resume.getActivities());
        Set<CourseResponse> courses = convertCourses(resume.getCourses());
        Set<ExperienceResponse> experiences = convertExperiences(resume.getExperiences());
        Set<EducationResponse> educations = convertEducations(resume.getEducations());
        Set<LicenseResponse> licenses = convertLicenses(resume.getLicenses());
        Set<LanguageResponse> languages = convertLanguages(resume.getLanguages());

        return ResumeResponse.builder()
            .name(resume.getName())
            .birth(resume.getBirth())
            .number(resume.getNumber())
            .email(resume.getEmail())
            .address(resume.getAddress())
            .gitAddress(resume.getGitAddress())
            .blogAddress(resume.getBlogAddress())
            .activities(activities)
            .courses(courses)
            .experiences(experiences)
            .educations(educations)
            .licenses(licenses)
            .languages(languages)
            .build();
    }

    private Set<ActivityResponse> convertActivities(Set<Activity> activities) {
        return activities.stream()
            .map(activity -> ActivityResponse.builder()  // ActivityResponse.builder() 사용
                .name(activity.getName())
                .history(activity.getHistory())
                .startDate(activity.getStartDate())
                .endDate(activity.getEndDate())
                .awards(activity.getAwards().stream()
                    .map(award -> AwardResponse.builder()  // AwardResponse.builder() 사용
                        .name(award.getName())
                        .institution(award.getInstitution())
                        .awardDate(award.getAwardDate())
                        .build())
                    .collect(Collectors.toSet()))
                .build())
            .collect(Collectors.toSet());
    }

    private Set<CourseResponse> convertCourses(Set<Course> courses) {
        return courses.stream()
            .map(course -> CourseResponse.builder()  // CourseResponse.builder() 사용
                .name(course.getName())
                .institution(course.getInstitution())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .build())
            .collect(Collectors.toSet());
    }

    private Set<ExperienceResponse> convertExperiences(Set<Experience> experiences) {
        return experiences.stream()
            .map(experience -> ExperienceResponse.builder()  // ExperienceResponse.builder() 사용
                .name(experience.getName())
                .department(experience.getDepartment())
                .position(experience.getPosition())
                .responsibility(experience.getResponsibility())
                .startDate(experience.getStartDate())
                .endDate(experience.getEndDate())
                .build())
            .collect(Collectors.toSet());
    }

    private Set<EducationResponse> convertEducations(Set<Education> educations) {
        return educations.stream()
            .map(education -> EducationResponse.builder()  // EducationResponse.builder() 사용
                .name(education.getName())
                .major(education.getMajor())
                .startDate(education.getStartDate())
                .endDate(education.getEndDate())
                .status(education.getStatus())
                .build())
            .collect(Collectors.toSet());
    }

    private Set<LicenseResponse> convertLicenses(Set<License> licenses) {
        return licenses.stream()
            .map(license -> LicenseResponse.builder()  // LicenseResponse.builder() 사용
                .name(license.getName())
                .institution(license.getInstitution())
                .certifiedDate(license.getCertifiedDate())
                .build())
            .collect(Collectors.toSet());
    }

    private Set<LanguageResponse> convertLanguages(Set<Language> languages) {
        return languages.stream()
            .map(language -> LanguageResponse.builder()  // LanguageResponse.builder() 사용
                .language(language.getLanguage())
                .name(language.getName())
                .result(language.getResult())
                .certifiedDate(language.getCertifiedDate())
                .build())
            .collect(Collectors.toSet());
    }

}
