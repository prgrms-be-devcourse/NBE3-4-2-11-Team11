package com.pofo.backend.domain.resume.course.repository;

import com.pofo.backend.domain.resume.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

    void deleteByResumeId(Long resumeId);

}