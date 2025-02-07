package com.pofo.backend.domain.resume.language.repository;

import com.pofo.backend.domain.resume.language.entity.Language;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Long> {
    void deleteByResumeId(Long resumeId);
}