package com.pofo.backend.domain.tool.repository;

import com.pofo.backend.domain.tool.entity.ResumeTool;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeToolRepository extends JpaRepository<ResumeTool, Long> {

    void deleteByResumeId(Long resumeId);
}
