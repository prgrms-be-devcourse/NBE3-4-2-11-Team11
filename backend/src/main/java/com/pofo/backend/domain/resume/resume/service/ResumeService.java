package com.pofo.backend.domain.resume.resume.service;

import com.pofo.backend.domain.resume.activity.activity.service.ActivityService;
import com.pofo.backend.domain.resume.course.service.CourseService;
import com.pofo.backend.domain.resume.education.service.EducationService;
import com.pofo.backend.domain.resume.experience.service.ExperienceService;
import com.pofo.backend.domain.resume.language.service.LanguageService;
import com.pofo.backend.domain.resume.license.service.LicenseService;
import com.pofo.backend.domain.resume.resume.dto.request.ResumeCreateRequest;
import com.pofo.backend.domain.resume.resume.dto.response.ResumeResponse;
import com.pofo.backend.domain.resume.resume.entity.Resume;
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException;
import com.pofo.backend.domain.resume.resume.exception.UnauthorizedActionException;
import com.pofo.backend.domain.resume.resume.mapper.ResumeMapper;
import com.pofo.backend.domain.resume.resume.repository.ResumeRepository;
import com.pofo.backend.domain.user.join.entity.User;
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
    private final ResumeMapper resumeMapper;

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

    @Transactional(readOnly = true)
    public ResumeResponse getResumeResponse(User user) {
        Resume resume = resumeRepository.findByUser(user)
            .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다."));

        return resumeMapper.resumeToResumeResponse(resume);
    }

}
