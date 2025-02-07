package com.pofo.backend.domain.resume.activity.activity.repository;

import com.pofo.backend.domain.resume.activity.activity.entity.Activity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    void deleteByResumeId(Long resumeId);
}