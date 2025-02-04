package com.pofo.backend.domain.resume.license.repository;

import com.pofo.backend.domain.resume.license.entity.License;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LicenseRepository extends JpaRepository<License, Long> {

    void deleteByResumeId(Long resumeId);

    List<License> findByResumeId(Long resumeId);
}
