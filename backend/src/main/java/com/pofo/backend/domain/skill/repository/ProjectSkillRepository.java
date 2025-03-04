package com.pofo.backend.domain.skill.repository;

import com.pofo.backend.domain.skill.entity.ProjectSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectSkillRepository extends JpaRepository<ProjectSkill, Long> {
    void deleteByProjectId(Long projectId);
    List<ProjectSkill> findByProjectId(Long projectId);

    void deleteByProjectIdIn(List<Long> projectIds);
}
