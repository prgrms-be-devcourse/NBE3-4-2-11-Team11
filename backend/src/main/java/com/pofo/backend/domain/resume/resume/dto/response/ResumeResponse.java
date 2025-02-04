package com.pofo.backend.domain.resume.resume.dto.response;

import com.pofo.backend.domain.resume.activity.activity.entity.Activity;
import com.pofo.backend.domain.resume.course.entity.Course;
import com.pofo.backend.domain.resume.experience.entity.Experience;
import com.pofo.backend.domain.resume.education.entity.Education;
import com.pofo.backend.domain.resume.license.entity.License;
import com.pofo.backend.domain.resume.language.entity.Language;
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
    private List<Activity> activities;
    private List<Course> courses;
    private List<Experience> experiences;
    private List<Education> educations;
    private List<License> licenses;
    private List<Language> languages;
}
