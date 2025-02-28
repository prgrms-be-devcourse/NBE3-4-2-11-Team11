package com.pofo.backend.domain.skill.entity;

import com.pofo.backend.common.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="skills")
@Getter
public class Skill extends BaseEntity {

    @Column(nullable = false ,  unique = true)
    private String name;
    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResumeSkill> resumeSkills = new ArrayList<>();
    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectSkill> projectSkills = new ArrayList<>();

    //테스트 코드에서 사용 가능한 public한 생성자 추가
    public Skill(String name){
        this.name = name;
    }
}