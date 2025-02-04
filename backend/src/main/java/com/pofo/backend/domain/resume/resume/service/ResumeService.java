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
        try {
            Resume resume = Resume.builder()
                .user(user)
                .name(resumeCreateRequest.getName())
                .birth(resumeCreateRequest.getBirth())
                .number(resumeCreateRequest.getNumber())
                .email(resumeCreateRequest.getEmail())
                .address(resumeCreateRequest.getAddress())
                .gitAddress(resumeCreateRequest.getGitAddress())
                .blogAddress(resumeCreateRequest.getBlogAddress())
                .build();

            resume = resumeRepository.save(resume);
            if (resumeCreateRequest.getActivities() != null) {
                activityService.addActivities(resume.getId(), resumeCreateRequest.getActivities());
            }
            if (resumeCreateRequest.getCourses() != null) {
                courseService.addCourses(resume.getId(), resumeCreateRequest.getCourses());
            }
            if (resumeCreateRequest.getExperiences() != null) {
                experienceService.addExperiences(resume.getId(), resumeCreateRequest.getExperiences());
            }
            if (resumeCreateRequest.getEducations() != null) {
                educationService.addEducations(resume.getId(), resumeCreateRequest.getEducations());
            }
            if (resumeCreateRequest.getLicenses() != null) {
                licenseService.addLicenses(resume.getId(), resumeCreateRequest.getLicenses());
            }
            if (resumeCreateRequest.getLanguages() != null) {
                languageService.addLanguages(resume.getId(), resumeCreateRequest.getLanguages());
            }
            return new ResumeCreateResponse(resume.getId(), "이력서 생성이 완료되었습니다.");
        } catch (DataAccessException e) {
            throw new ResumeCreationException("이력서 생성 중 데이터베이스 오류가 발생했습니다.");
        } catch (Exception e) {
            throw new ResumeCreationException("서버 오류로 인해 이력서 생성에 실패했습니다.");
        }
    }

    @Transactional
    public ResumeCreateResponse updateResume(Long resumeId, ResumeCreateRequest resumeCreateRequest, User user) {
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new ResumeCreationException("이력서가 존재하지 않습니다."));

        if (!resume.getUser().equals(user)) {
            throw new UnauthorizedActionException("이력서를 수정할 권한이 없습니다.");
        }

        try {
            resume = resume.toBuilder()
                .name(resumeCreateRequest.getName())
                .birth(resumeCreateRequest.getBirth())
                .number(resumeCreateRequest.getNumber())
                .email(resumeCreateRequest.getEmail())
                .address(resumeCreateRequest.getAddress())
                .gitAddress(resumeCreateRequest.getGitAddress())
                .blogAddress(resumeCreateRequest.getBlogAddress())
                .build();
            if (resumeCreateRequest.getActivities() != null) {
                activityService.updateActivities(resumeId, resumeCreateRequest.getActivities());
            }
            if (resumeCreateRequest.getCourses() != null) {
                courseService.updateCourses(resume.getId(), resumeCreateRequest.getCourses());
            }
            if (resumeCreateRequest.getExperiences() != null) {
                experienceService.updateExperiences(resume.getId(), resumeCreateRequest.getExperiences());
            }
            if (resumeCreateRequest.getEducations() != null) {
                educationService.updateEducations(resume.getId(), resumeCreateRequest.getEducations());
            }
            if (resumeCreateRequest.getLicenses() != null) {
                licenseService.updateLicenses(resume.getId(), resumeCreateRequest.getLicenses());
            }
            if (resumeCreateRequest.getLanguages() != null) {
                languageService.updateLanguages(resume.getId(), resumeCreateRequest.getLanguages());
            }
            resumeRepository.save(resume);
            return new ResumeCreateResponse(resume.getId(), "이력서 수정이 완료되었습니다.");
        } catch (DataAccessException e) {
            throw new ResumeCreationException("이력서 수정 중 데이터베이스 오류가 발생했습니다.");
        }
    }

    @Transactional
    public void deleteResume(Long resumeId, User user) {
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new ResumeCreationException("이력서가 존재하지 않습니다."));

        if (!resume.getUser().equals(user)) {
            throw new UnauthorizedActionException("이력서를 삭제할 권한이 없습니다.");
        }

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
        resumeResponse.setActivities(activityService.getActivitiesByResumeId(resume.getId()));
        resumeResponse.setCourses(courseService.getCoursesByResumeId(resume.getId()));
        resumeResponse.setExperiences(experienceService.getExperiencesByResumeId(resume.getId()));
        resumeResponse.setEducations(educationService.getEducationsByResumeId(resume.getId()));
        resumeResponse.setLicenses(licenseService.getLicensesByResumeId(resume.getId()));
        resumeResponse.setLanguages(languageService.getLanguagesByResumeId(resume.getId()));
        return resumeMapper.resumeToResumeResponse(resume);
    }
}
