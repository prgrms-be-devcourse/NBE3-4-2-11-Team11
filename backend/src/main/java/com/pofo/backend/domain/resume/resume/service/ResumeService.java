package com.pofo.backend.domain.resume.resume.service;


import com.pofo.backend.domain.resume.activity.activity.service.ActivityService;
import com.pofo.backend.domain.resume.course.service.CourseService;
import com.pofo.backend.domain.resume.experience.service.ExperienceService;
import com.pofo.backend.domain.resume.education.service.EducationService;
import com.pofo.backend.domain.resume.license.service.LicenseService;
import com.pofo.backend.domain.resume.resume.dto.request.ResumeCreateRequest;
import com.pofo.backend.domain.resume.resume.dto.response.ResumeCreateResponse;
import com.pofo.backend.domain.resume.resume.dto.response.ResumeResponse;
import com.pofo.backend.domain.resume.resume.entity.Resume;
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException;
import com.pofo.backend.domain.resume.resume.exception.UnauthorizedActionException;
import com.pofo.backend.domain.resume.resume.mapper.ResumeMapper;
import com.pofo.backend.domain.resume.resume.repository.ResumeRepository;

import com.pofo.backend.domain.user.join.entity.User;
import com.pofo.backend.domain.resume.language.service.LanguageService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final ResumeMapper resumeMapper;
    private final ActivityService activityService;
    private final CourseService courseService;
    private final ExperienceService experienceService;
    private final EducationService educationService;
    private final LicenseService licenseService;
    private final LanguageService languageService;

    @Transactional
    public ResumeCreateResponse createResume(ResumeCreateRequest resumeCreateRequest, User user) {
        Resume resume = buildResume(resumeCreateRequest, user);

        resume = saveResumeAndRelatedEntities(resume, resumeCreateRequest);

        return new ResumeCreateResponse(resume.getId(), "이력서 생성이 완료되었습니다.");
    }

    @Transactional
    public ResumeCreateResponse updateResume(Long resumeId, ResumeCreateRequest resumeCreateRequest,
        User user) {
        Resume resume = findResumeByIdAndCheckOwnership(resumeId, user);

        updateResumeFields(resume, resumeCreateRequest);

        resume = saveResumeAndRelatedEntities(resume, resumeCreateRequest);

        return new ResumeCreateResponse(resume.getId(), "이력서 수정이 완료되었습니다.");
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

    @Transactional
    public ResumeResponse getResumeByUser(User user) {
        Resume resume = resumeRepository.findByUser(user)
            .orElseThrow(() -> new ResumeCreationException("이력서가 존재하지 않습니다."));

        ResumeResponse resumeResponse = resumeMapper.resumeToResumeResponse(resume);
        populateResumeDetails(resumeResponse, resume.getId());

        return resumeResponse;
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
            activityService.addActivities(resume.getId(), request.getActivities());
        }
        if (request.getCourses() != null) {
            courseService.addCourses(resume.getId(), request.getCourses());
        }
        if (request.getExperiences() != null) {
            experienceService.addExperiences(resume.getId(), request.getExperiences());
        }
        if (request.getEducations() != null) {
            educationService.addEducations(resume.getId(), request.getEducations());
        }
        if (request.getLicenses() != null) {
            licenseService.addLicenses(resume.getId(), request.getLicenses());
        }
        if (request.getLanguages() != null) {
            languageService.addLanguages(resume.getId(), request.getLanguages());
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

    private void populateResumeDetails(ResumeResponse resumeResponse, Long resumeId) {
        resumeResponse.setActivities(activityService.getActivitiesByResumeId(resumeId));
        resumeResponse.setCourses(courseService.getCoursesByResumeId(resumeId));
        resumeResponse.setExperiences(experienceService.getExperiencesByResumeId(resumeId));
        resumeResponse.setEducations(educationService.getEducationsByResumeId(resumeId));
        resumeResponse.setLicenses(licenseService.getLicensesByResumeId(resumeId));
        resumeResponse.setLanguages(languageService.getLanguagesByResumeId(resumeId));
    }
}
