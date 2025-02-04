package com.pofo.backend.domain.resume.resume.service;


import com.pofo.backend.domain.resume.course.service.CourseService;
import com.pofo.backend.domain.resume.experience.service.ExperienceService;
import com.pofo.backend.domain.resume.education.service.EducationService;
import com.pofo.backend.domain.resume.license.service.LicenseService;
import com.pofo.backend.domain.resume.resume.dto.request.ResumeRequest;
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
    private final CourseService courseService;
    private final ExperienceService experienceService;
    private final EducationService educationService;
    private final LicenseService licenseService;
    private final LanguageService languageService;

    @Transactional
    public ResumeCreateResponse createResume(ResumeRequest resumeRequest, User user) {
        try {
            Resume resume = Resume.builder()
                .user(user)
                .name(resumeRequest.getName())
                .birth(resumeRequest.getBirth())
                .number(resumeRequest.getNumber())
                .email(resumeRequest.getEmail())
                .address(resumeRequest.getAddress())
                .gitAddress(resumeRequest.getGitAddress())
                .blogAddress(resumeRequest.getBlogAddress())
                .build();
            resume = resumeRepository.save(resume);
            if (resumeRequest.getCourses() != null) {
                courseService.addCourses(resume.getId(), resumeRequest.getCourses());
            }
            if (resumeRequest.getExperiences() != null) {
                experienceService.addExperiences(resume.getId(), resumeRequest.getExperiences());
            }
            if (resumeRequest.getEducations() != null) {
                educationService.addEducations(resume.getId(), resumeRequest.getEducations());
            }
            if (resumeRequest.getLicenses() != null) {
                licenseService.addLicenses(resume.getId(), resumeRequest.getLicenses());
            }
            if (resumeRequest.getLanguages() != null) {
                languageService.addLanguages(resume.getId(), resumeRequest.getLanguages());
            }
            return new ResumeCreateResponse(resume.getId(), "이력서 생성이 완료되었습니다.");
        } catch (DataAccessException e) {
            throw new ResumeCreationException("이력서 생성 중 데이터베이스 오류가 발생했습니다.");
        } catch (Exception e) {
            throw new ResumeCreationException("서버 오류로 인해 이력서 생성에 실패했습니다.");
        }
    }

    @Transactional
    public ResumeCreateResponse updateResume(Long resumeId, ResumeRequest resumeRequest, User user) {
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new ResumeCreationException("이력서가 존재하지 않습니다."));

        if (!resume.getUser().equals(user)) {
            throw new UnauthorizedActionException("이력서를 수정할 권한이 없습니다.");
        }

        try {
            resume = resume.toBuilder()
                .name(resumeRequest.getName())
                .birth(resumeRequest.getBirth())
                .number(resumeRequest.getNumber())
                .email(resumeRequest.getEmail())
                .address(resumeRequest.getAddress())
                .gitAddress(resumeRequest.getGitAddress())
                .blogAddress(resumeRequest.getBlogAddress())
                .build();
            if (resumeRequest.getCourses() != null) {
                courseService.updateCourses(resume.getId(), resumeRequest.getCourses());
            }
            if (resumeRequest.getExperiences() != null) {
                experienceService.updateExperiences(resume.getId(), resumeRequest.getExperiences());
            }
            if (resumeRequest.getEducations() != null) {
                educationService.updateEducations(resume.getId(), resumeRequest.getEducations());
            }
            if (resumeRequest.getLicenses() != null) {
                licenseService.updateLicenses(resume.getId(), resumeRequest.getLicenses());
            }
            if (resumeRequest.getLanguages() != null) {
                languageService.updateLanguages(resume.getId(), resumeRequest.getLanguages());
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
        resumeResponse.setCourses(courseService.getCoursesByResumeId(resume.getId()));
        resumeResponse.setExperiences(experienceService.getExperiencesByResumeId(resume.getId()));
        resumeResponse.setEducations(educationService.getEducationsByResumeId(resume.getId()));
        resumeResponse.setLicenses(licenseService.getLicensesByResumeId(resume.getId()));
        resumeResponse.setLanguages(languageService.getLanguagesByResumeId(resume.getId()));
        return resumeResponse;
    }
}
