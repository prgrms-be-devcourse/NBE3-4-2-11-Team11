package com.pofo.backend.domain.resume.activity.award.repository;

import com.pofo.backend.domain.resume.activity.award.entity.Award;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AwardRepository extends JpaRepository<Award, Long> {

    void deleteByActivityId(Long resumeId);
    List<Award> findByActivityId(Long resumeId);
}