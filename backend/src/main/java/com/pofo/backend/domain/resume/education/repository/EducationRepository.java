package com.pofo.backend.domain.resume.education.repository;

import com.pofo.backend.domain.resume.education.entity.Education;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EducationRepository extends JpaRepository<Education, Long> {
    void deleteByResumeId(Long resumeId);
    List<Education> findByResumeId(Long resumeId);
}
