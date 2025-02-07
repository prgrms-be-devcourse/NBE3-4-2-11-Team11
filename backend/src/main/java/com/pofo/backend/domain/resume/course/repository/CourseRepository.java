package com.pofo.backend.domain.resume.course.repository;

import com.pofo.backend.domain.resume.course.entity.Course;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

    void deleteByResumeId(Long resumeId);

    List<Course> findByResumeId(Long resumeId);
}