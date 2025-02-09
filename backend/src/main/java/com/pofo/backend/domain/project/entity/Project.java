package com.pofo.backend.domain.project.entity;

import com.pofo.backend.common.jpa.entity.BaseTime;
import com.pofo.backend.domain.skill.entity.ProjectSkill;
import com.pofo.backend.domain.tool.entity.ProjectTool;
import com.pofo.backend.domain.user.join.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="projects")
@Getter
@Setter
public class Project extends BaseTime {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private LocalDate startDate;
    @Column(nullable = false)
    private LocalDate endDate;
    @Column(nullable = false)
    private int memberCount;
    @Column(nullable = false)
    private String position;

    private String repositoryLink;

    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String imageUrl;

    @OneToMany(mappedBy = "tool", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectTool> projectTools = new ArrayList<>();
    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectSkill> projectSkills = new ArrayList<>();

    public void update(String name, LocalDate startDate, LocalDate endDate, int memberCount,
                       String position, String repositoryLink, String description, String imageUrl,
                       List<ProjectSkill> skills, List<ProjectTool> tools) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.memberCount = memberCount;
        this.position = position;
        this.repositoryLink = repositoryLink;
        this.description = description;
        this.imageUrl = imageUrl;

        // 기존 정보 유지하며 새로운 데이터 추가 또는 갱신
        Map<Long, ProjectSkill> existingSkills = this.projectSkills.stream()
                .collect(Collectors.toMap(ps -> ps.getSkill().getId(), ps -> ps));

        skills.forEach(skill -> existingSkills.put(skill.getSkill().getId(), skill));
        this.projectSkills.clear();
        this.projectSkills.addAll(existingSkills.values());

        Map<Long, ProjectTool> existingTools = this.projectTools.stream()
                .collect(Collectors.toMap(pt -> pt.getTool().getId(), pt -> pt));

        tools.forEach(tool -> existingTools.put(tool.getTool().getId(), tool));
        this.projectTools.clear();
        this.projectTools.addAll(existingTools.values());
    }
}
