package com.pofo.backend.domain.project.entity;

import com.pofo.backend.common.jpa.entity.BaseTime;
import com.pofo.backend.domain.user.join.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    public void update(String name, LocalDate startDate, LocalDate endDate, int memberCount,
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
}
