package com.pofo.backend.domain.skill.repository;

import com.pofo.backend.domain.skill.entity.ProjectSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectSkillRepository extends JpaRepository<ProjectSkill, Long> {

}
