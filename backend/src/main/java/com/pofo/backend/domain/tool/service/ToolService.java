package com.pofo.backend.domain.tool.service;

import com.pofo.backend.domain.project.entity.Project;
import com.pofo.backend.domain.project.exception.ProjectCreationException;
import com.pofo.backend.domain.project.repository.ProjectRepository;
import com.pofo.backend.domain.tool.dto.ToolProjection;
import com.pofo.backend.domain.tool.entity.ProjectTool;
import com.pofo.backend.domain.tool.entity.Tool;
import com.pofo.backend.domain.tool.repository.ProjectToolRepository;
import com.pofo.backend.domain.tool.repository.ToolRepository;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ToolService {

    private final ToolRepository toolRepository;
    private final ProjectRepository projectRepository;
    private final ProjectToolRepository projectToolRepository;


    private final EntityManager entityManager;

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

    public void addProjectTools(Long projectId, List<String> toolNames) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectCreationException("404","해당 프로젝트를 찾을 수 없습니다."));

        List<ProjectTool> projectTools = toolNames.stream()
                .map(toolName -> new ProjectTool(project, getToolByName(toolName))) // ✅ 이름을 ID로 변환
                .collect(Collectors.toList());

        projectToolRepository.saveAll(projectTools);

        entityManager.flush();
    }

    @Transactional
    public void updateProjectTools(Long projectId, List<String> toolNames) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectCreationException("404", "해당 프로젝트를 찾을 수 없습니다."));

        // 기존 도구 삭제 (DB + 영속성 컨텍스트 동기화)
        projectToolRepository.deleteByProjectId(projectId);

        // 새로운 도구 추가
        List<ProjectTool> updatedTools = toolNames.stream()
                .map(toolName -> new ProjectTool(project, getToolByName(toolName)))
                .collect(Collectors.toList());

        project.getProjectTools().clear();  // 영속성 컨텍스트에서도 제거
        project.getProjectTools().addAll(updatedTools);
        projectToolRepository.saveAll(updatedTools);

        entityManager.flush();
        entityManager.clear();
    }

    public void deleteProjectTools(List<Long> projectIds) {
        projectToolRepository.deleteByProjectIdIn(projectIds);
    }

    public Tool getToolByName(String toolName) {
        return toolRepository.findByName(toolName)
                .orElseThrow(() -> new IllegalArgumentException("해당 도구를 찾을 수 없습니다: " + toolName));
    }

    // 저장된 모든 도구 조회
    public List<ToolProjection> getAllTools() {
        return toolRepository.findAllByProjection();
    }

    public List<String> getProjectToolNames(Long projectId) {
        return projectToolRepository.findByProjectId(projectId)
                .stream()
                .map(pt -> pt.getTool().getName())
                .collect(Collectors.toList());
    }


}
