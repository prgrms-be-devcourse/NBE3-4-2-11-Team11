package com.pofo.backend.domain.skill.service;

import com.pofo.backend.domain.resume.resume.entity.Resume;
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException;
import com.pofo.backend.domain.resume.resume.repository.ResumeRepository;
import com.pofo.backend.domain.skill.entity.ResumeSkill;
import com.pofo.backend.domain.skill.entity.Skill;
import com.pofo.backend.domain.skill.repository.ResumeSkillRepository;
import com.pofo.backend.domain.skill.repository.SkillRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ResumeSkillService {

    private final ResumeSkillRepository resumeSkillRepository;
    private final ResumeRepository resumeRepository;
    private final SkillRepository skillRepository;

    public void updateSkills(Long resumeId, List<Long> skills) {
        resumeSkillRepository.deleteByResumeId(resumeId);
        addSkills(resumeId, skills);
    }

    public void addSkills(Long resumeId, List<Long> skillIds) {
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new ResumeCreationException("이력서를 찾을 수 없습니다."));
        List<Skill> skills = skillRepository.findAllById(skillIds);

        List<ResumeSkill> resumeSkills = skills.stream()
            .map(skill -> ResumeSkill.builder()
                .resume(resume)
                .skill(skill)
                .build())
            .collect(Collectors.toList());

        resumeSkillRepository.saveAll(resumeSkills);
    }
}
