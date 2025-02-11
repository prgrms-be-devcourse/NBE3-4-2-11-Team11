package com.pofo.backend.domain.skill.service;

import com.pofo.backend.domain.project.exception.ProjectCreationException;
import com.pofo.backend.domain.skill.dto.SkillProjection;
import com.pofo.backend.domain.skill.entity.Skill;
import com.pofo.backend.domain.skill.repository.SkillRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;


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

    public Skill getSkillByName(String skillName) {
        return skillRepository.findByName(skillName)
                .orElseThrow(() -> ProjectCreationException.notFound("해당 기술을 찾을 수 없습니다: " + skillName));
    }

    // 저장된 모든 기술 조회
    public List<SkillProjection> getAllSkills() {
        return skillRepository.findAllByProjection();
    }


}
