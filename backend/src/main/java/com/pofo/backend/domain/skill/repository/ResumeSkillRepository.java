package com.pofo.backend.domain.skill.repository;

import com.pofo.backend.domain.skill.entity.ResumeSkill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeSkillRepository extends JpaRepository<ResumeSkill, Long> {

    void deleteByResumeId(Long resumeId);
}
