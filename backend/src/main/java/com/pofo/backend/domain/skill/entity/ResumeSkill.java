package com.pofo.backend.domain.skill.entity;

import com.pofo.backend.common.jpa.entity.BaseEntity;
import com.pofo.backend.domain.resume.resume.entity.Resume;
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
@Table(name="resume_skills")
@Getter
public class ResumeSkill extends BaseEntity {

    @ManyToOne
    @JoinColumn(name="resume_id", nullable = false)
    private Resume resume;

    @ManyToOne
    @JoinColumn(name="skill_id", nullable = false)
    private Skill skill;

}