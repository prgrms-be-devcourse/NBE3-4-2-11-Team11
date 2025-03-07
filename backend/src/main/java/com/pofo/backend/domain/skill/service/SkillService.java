package com.pofo.backend.domain.skill.service;

import com.pofo.backend.domain.project.entity.Project;
import com.pofo.backend.domain.project.exception.ProjectCreationException;
import com.pofo.backend.domain.project.repository.ProjectRepository;
import com.pofo.backend.domain.skill.dto.SkillProjection;
import com.pofo.backend.domain.skill.entity.ProjectSkill;
import com.pofo.backend.domain.skill.entity.Skill;
import com.pofo.backend.domain.skill.repository.ProjectSkillRepository;
import com.pofo.backend.domain.skill.repository.SkillRepository;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final ProjectRepository projectRepository;
    private final ProjectSkillRepository projectSkillRepository;

    private final EntityManager entityManager;

    public void save() {
        if (skillRepository.count() > 0) return;
        List<String> skillNames = List.of(
            // 프로그래밍 언어
            "Java", "Python", "JavaScript", "TypeScript", "Kotlin", "Swift", "C++",

            // 백엔드 기술
            "Spring Boot", "Node.js", "Express", "NestJS", "Django", "FastAPI", "REST API", "GraphQL",

            // 프론트엔드 기술
            "React.js", "Vue.js", "Next.js", "HTML", "CSS", "Tailwind CSS",

            // 데이터베이스 & 캐싱
            "MySQL", "PostgreSQL", "MongoDB", "Redis",

            // DevOps & 배포
            "Docker", "Kubernetes", "AWS", "GCP", "Azure", "GitHub Actions", "Jenkins",

            // 모바일 개발
            "Android", "iOS", "React Native", "Flutter",

            // 테스트 & 품질 관리
            "JUnit", "Jest", "Cypress", "Selenium",

            // 협업 및 버전 관리
            "Git", "GitHub", "GitLab", "Jira", "Notion"
        );

        for (String name : skillNames) {
            Skill skill = Skill.builder()
                .name(name)
                .build();
            skillRepository.save(skill);
        }
    }

    public void addProjectSkills(Long projectId, List<String> skillNames) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectCreationException("404", "해당 프로젝트를 찾을 수 없습니다."));

        List<ProjectSkill> projectSkills = skillNames.stream()
                .map(skillName -> new ProjectSkill(project, getSkillByName(skillName)))
                .collect(Collectors.toList());

        projectSkillRepository.saveAll(projectSkills);
    }

    @Transactional
    public void updateProjectSkills(Long projectId, List<String> skillNames) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectCreationException("404", "해당 프로젝트를 찾을 수 없습니다."));

        // 기존 스킬 삭제 (DB + 영속성 컨텍스트 동기화)
        projectSkillRepository.deleteByProjectId(projectId);

        // 새로운 스킬 추가
        List<ProjectSkill> updatedSkills = skillNames.stream()
                .map(skillName -> new ProjectSkill(project, getSkillByName(skillName)))
                .collect(Collectors.toList());

        project.getProjectSkills().clear();  // 영속성 컨텍스트에서도 제거
        project.getProjectSkills().addAll(updatedSkills);
        projectSkillRepository.saveAll(updatedSkills);

        entityManager.flush();
        entityManager.clear();
    }

    public void deleteProjectSkills(List<Long> projectIds) {
        projectSkillRepository.deleteByProjectIdIn(projectIds);
    }


    public Skill getSkillByName(String skillName) {
        return skillRepository.findByName(skillName)
                .orElseThrow(() -> new IllegalArgumentException("해당 스킬을 찾을 수 없습니다: " + skillName));
    }

    // 저장된 모든 기술 조회
    public List<SkillProjection> getAllSkills() {
        return skillRepository.findAllByProjection();
    }

    public List<String> getProjectSkillNames(Long projectId) {
        return projectSkillRepository.findByProjectId(projectId)
                .stream()
                .map(ps -> ps.getSkill().getName())
                .collect(Collectors.toList());
    }



}
