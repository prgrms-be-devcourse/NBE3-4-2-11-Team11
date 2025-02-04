package com.pofo.backend.domain.resume.resume.dto.response;

import com.pofo.backend.domain.resume.course.entity.Course;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ResumeResponse {
    private String name;
    private LocalDate birth;
    private String number;
    private String email;
    private String address;
    private String gitAddress;
    private String blogAddress;
    private List<Course> courses;
}
