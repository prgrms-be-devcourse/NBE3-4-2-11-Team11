package com.pofo.backend.domain.resume.activity.award.repository;

import com.pofo.backend.domain.resume.activity.award.entity.Award;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AwardRepository extends JpaRepository<Award, Long> {

}