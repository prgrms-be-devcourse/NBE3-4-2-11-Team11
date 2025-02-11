package com.pofo.backend.domain.skill.repository;

import com.pofo.backend.domain.skill.dto.SkillProjection;
import com.pofo.backend.domain.skill.entity.Skill;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findByName(String name);

    @Query("SELECT new com.pofo.backend.domain.skill.dto.SkillProjectionImpl(s.id, s.name) FROM Skill s")
    List<SkillProjection> findAllByProjection();
}
