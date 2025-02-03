package com.pofo.backend.domain.skill.entity;

import com.pofo.backend.common.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="skills")
@Getter
@Setter
public class Skill extends BaseEntity {

    private String skill;


    public Skill(long id, String skill) {
        super();
        this.skill = skill;
    }
}
