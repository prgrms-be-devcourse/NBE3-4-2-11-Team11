package com.pofo.backend.domain.resume.experience.repository;

import com.pofo.backend.domain.resume.experience.entity.Experience;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    void deleteByResumeId(Long resumeId);
}
