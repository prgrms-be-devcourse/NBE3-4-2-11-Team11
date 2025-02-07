package com.pofo.backend.domain.resume.license.service;

import com.pofo.backend.domain.resume.license.dto.LicenseRequest;
import com.pofo.backend.domain.resume.license.entity.License;
import com.pofo.backend.domain.resume.license.repository.LicenseRepository;
import com.pofo.backend.domain.resume.resume.entity.Resume;
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException;
import com.pofo.backend.domain.resume.resume.repository.ResumeRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LicenseService {

    private final LicenseRepository licenseRepository;
    private final ResumeRepository resumeRepository;

    public void addLicenses(Long resumeId, List<LicenseRequest> licenses) {
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new ResumeCreationException("이력서를 찾을 수 없습니다."));

        List<License> licenseEntities = licenses.stream()
            .map(licenseRequest -> License.builder()
                .name(licenseRequest.getName())
                .institution(licenseRequest.getInstitution())
                .certifiedDate(licenseRequest.getCertifiedDate())
                .resume(resume)
                .build())
            .collect(Collectors.toList());

        licenseRepository.saveAll(licenseEntities);
    }

    public void updateLicenses(Long resumeId, List<LicenseRequest> licenses) {
        licenseRepository.deleteByResumeId(resumeId);
        addLicenses(resumeId, licenses);
    }
}
