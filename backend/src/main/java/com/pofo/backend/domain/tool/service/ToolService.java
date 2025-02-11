package com.pofo.backend.domain.tool.service;

import com.pofo.backend.domain.project.exception.ProjectCreationException;
import com.pofo.backend.domain.tool.dto.ToolProjection;
import com.pofo.backend.domain.tool.entity.Tool;
import com.pofo.backend.domain.tool.repository.ToolRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ToolService {
    private final ToolRepository toolRepository;


    public void save() {
        if (toolRepository.count() > 0) return;
        List<String> toolNames = List.of(
            "IntelliJ IDEA", "Visual Studio Code", "Eclipse", "PyCharm", "Android Studio",

            // 디자인 & 프로토타이핑
            "Figma", "Sketch",

            // 협업 및 프로젝트 관리
            "GitHub", "GitLab", "Bitbucket", "Jira", "Notion", "Slack",

            // DevOps & CI/CD
            "Docker", "Kubernetes", "GitHub Actions", "Jenkins",

            // 데이터베이스 관리
            "MySQL Workbench", "DBeaver",

            // API 개발 및 테스트
            "Postman", "Swagger"

        );

        for(String name : toolNames){
            Tool tool = Tool.builder()
                .name(name)
                .build();

            toolRepository.save(tool);
        }

    }

    public Tool getToolByName(String toolName) {
        return toolRepository.findByName(toolName)
                .orElseThrow(() -> ProjectCreationException.notFound("해당 도구를 찾을 수 없습니다: " + toolName));
    }

    // 저장된 모든 도구 조회
    public List<ToolProjection> getAllTools() {
        return toolRepository.findAllByProjection();
    }
}
