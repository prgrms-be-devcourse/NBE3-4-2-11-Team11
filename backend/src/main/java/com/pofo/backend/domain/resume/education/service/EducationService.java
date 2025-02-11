package com.pofo.backend.domain.resume.education.service;

import com.pofo.backend.domain.resume.education.dto.EducationRequest;
import com.pofo.backend.domain.resume.education.entity.Education;
import com.pofo.backend.domain.resume.education.repository.EducationRepository;
import com.pofo.backend.domain.resume.resume.entity.Resume;
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException;
import com.pofo.backend.domain.resume.resume.repository.ResumeRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EducationService {

    private final EducationRepository educationRepository;
    private final ResumeRepository resumeRepository;

    public void addEducations(Long resumeId, List<EducationRequest> educationRequests) {
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new ResumeCreationException("이력서를 찾을 수 없습니다."));

        List<Education> educationEntities = educationRequests.stream()
            .map(educationRequest -> Education.builder()
                .name(educationRequest.getName())
                .major(educationRequest.getMajor())
                .startDate(educationRequest.getStartDate())
                .endDate(educationRequest.getEndDate())
                .status(educationRequest.getStatusEnum())
                .resume(resume)
                .build())
            .collect(Collectors.toList());

        educationRepository.saveAll(educationEntities);
    }

    public void updateEducations(Long resumeId, List<EducationRequest> educationRequests) {
        educationRepository.deleteByResumeId(resumeId);
        addEducations(resumeId, educationRequests);
    }

}
