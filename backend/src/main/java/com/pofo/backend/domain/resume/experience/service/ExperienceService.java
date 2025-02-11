package com.pofo.backend.domain.resume.experience.service;

import com.pofo.backend.domain.resume.experience.dto.ExperienceRequest;
import com.pofo.backend.domain.resume.experience.entity.Experience;
import com.pofo.backend.domain.resume.experience.repository.ExperienceRepository;
import com.pofo.backend.domain.resume.resume.entity.Resume;
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException;
import com.pofo.backend.domain.resume.resume.repository.ResumeRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final ResumeRepository resumeRepository;

    public void addExperiences(Long resumeId, List<ExperienceRequest> experienceRequests) {
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new ResumeCreationException("이력서를 찾을 수 없습니다."));

        List<Experience> experienceEntities = experienceRequests.stream()
            .map(experienceRequest -> Experience.builder()
                .name(experienceRequest.getName())
                .department(experienceRequest.getDepartment())
                .position(experienceRequest.getPosition())
                .responsibility(experienceRequest.getResponsibility())
                .startDate(experienceRequest.getStartDate())
                .endDate(experienceRequest.getEndDate())
                .resume(resume)
                .build())
            .collect(Collectors.toList());

        experienceRepository.saveAll(experienceEntities);
    }

    public void updateExperiences(Long resumeId, List<ExperienceRequest> experienceRequests) {
        experienceRepository.deleteByResumeId(resumeId);
        addExperiences(resumeId, experienceRequests);
    }

}
