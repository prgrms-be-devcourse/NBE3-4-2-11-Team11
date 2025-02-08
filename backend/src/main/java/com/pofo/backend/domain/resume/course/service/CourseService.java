package com.pofo.backend.domain.resume.course.service;

import com.pofo.backend.domain.resume.course.dto.CourseRequest;
import com.pofo.backend.domain.resume.course.entity.Course;
import com.pofo.backend.domain.resume.course.repository.CourseRepository;
import com.pofo.backend.domain.resume.resume.entity.Resume;
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException;
import com.pofo.backend.domain.resume.resume.repository.ResumeRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final ResumeRepository resumeRepository;

    public void addCourses(Long resumeId, List<CourseRequest> courseRequests) {
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new ResumeCreationException("이력서를 찾을 수 없습니다."));

        List<Course> courseEntities = courseRequests.stream()
            .map(courseRequest -> Course.builder()
                .name(courseRequest.getName())
                .institution(courseRequest.getInstitution())
                .startDate(courseRequest.getStartDate())
                .endDate(courseRequest.getEndDate())
                .resume(resume)
                .build())
            .collect(Collectors.toList());

        courseRepository.saveAll(courseEntities);
    }

    public void updateCourses(Long resumeId, List<CourseRequest> courseRequests) {
        courseRepository.deleteByResumeId(resumeId);
        addCourses(resumeId, courseRequests);
    }

}
