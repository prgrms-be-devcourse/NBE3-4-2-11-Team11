package com.pofo.backend.domain.resume.language.service;

import com.pofo.backend.domain.resume.language.dto.LanguageRequest;
import com.pofo.backend.domain.resume.language.entity.Language;
import com.pofo.backend.domain.resume.language.repository.LanguageRepository;
import com.pofo.backend.domain.resume.resume.entity.Resume;
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException;
import com.pofo.backend.domain.resume.resume.repository.ResumeRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LanguageService {

    private final LanguageRepository languageRepository;
    private final ResumeRepository resumeRepository;

    public void addLanguages(Long resumeId, List<LanguageRequest> languages) {
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new ResumeCreationException("이력서를 찾을 수 없습니다."));

        List<Language> languageEntities = languages.stream()
            .map(languageRequest -> Language.builder()
                .language(languageRequest.getLanguage())
                .result(languageRequest.getResult())
                .certifiedDate(languageRequest.getCertifiedDate())
                .name(languageRequest.getName())
                .resume(resume)
                .build())
            .collect(Collectors.toList());

        languageRepository.saveAll(languageEntities);
    }

    public void updateLanguages(Long resumeId, List<LanguageRequest> languages) {
        languageRepository.deleteByResumeId(resumeId);
        addLanguages(resumeId, languages);
    }

}
