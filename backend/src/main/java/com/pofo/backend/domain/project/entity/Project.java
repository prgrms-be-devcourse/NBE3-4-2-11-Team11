package com.pofo.backend.domain.project.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pofo.backend.common.jpa.entity.BaseTime;
import com.pofo.backend.domain.skill.entity.ProjectSkill;
import com.pofo.backend.domain.tool.entity.ProjectTool;
import com.pofo.backend.domain.user.join.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @Lob
    @Column(columnDefinition = "TEXT",nullable = false)
    private String description;


    @Column(nullable = false)
    private String imageUrl;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // ✅ JSON에서 thumbnailPath를 무시
    private String thumbnailPath; // 서버에 저장된 썸네일 파일 경로


    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectTool> projectTools = new ArrayList<>();
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectSkill> projectSkills = new ArrayList<>();

    public void updateBasicInfo(String name, LocalDate startDate, LocalDate endDate, int memberCount,
                       String position, String repositoryLink, String description, String imageUrl) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.memberCount = memberCount;
        this.position = position;
        this.repositoryLink = repositoryLink;
        this.description = description;
        this.imageUrl = imageUrl;

    }

    @Column(nullable = false)
    private boolean isDeleted = false; // 기본값 false: 활성화, true: 휴지통 상태
}
