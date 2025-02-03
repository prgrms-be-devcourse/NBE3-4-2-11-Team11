package com.pofo.backend.domain.skill.entity;

import com.pofo.backend.common.jpa.entity.BaseEntity;
import com.pofo.backend.domain.project.entity.Project;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="project_skills")
@Getter
public class ProjectSkill extends BaseEntity {

    @ManyToOne
    @JoinColumn(name="project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name="skill_id", nullable = false)
    private Skill skill;

}
