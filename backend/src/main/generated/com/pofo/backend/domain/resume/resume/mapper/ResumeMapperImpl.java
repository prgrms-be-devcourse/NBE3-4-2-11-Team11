package com.pofo.backend.domain.resume.resume.mapper;

import com.pofo.backend.domain.resume.activity.activity.entity.Activity;
import com.pofo.backend.domain.resume.course.entity.Course;
import com.pofo.backend.domain.resume.education.entity.Education;
import com.pofo.backend.domain.resume.experience.entity.Experience;
import com.pofo.backend.domain.resume.language.entity.Language;
import com.pofo.backend.domain.resume.license.entity.License;
import com.pofo.backend.domain.resume.resume.dto.response.ResumeResponse;
import com.pofo.backend.domain.resume.resume.entity.Resume;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-04T22:08:42+0900",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 21.0.5 (Oracle Corporation)"
)
@Component
public class ResumeMapperImpl implements ResumeMapper {

    @Override
    public ResumeResponse resumeToResumeResponse(Resume resume) {
        if ( resume == null ) {
            return null;
        }

        String name = null;
        LocalDate birth = null;
        String number = null;
        String email = null;
        String address = null;
        String gitAddress = null;
        String blogAddress = null;
        List<Activity> activities = null;
        List<Course> courses = null;
        List<Experience> experiences = null;
        List<Education> educations = null;
        List<License> licenses = null;
        List<Language> languages = null;

        name = resume.getName();
        birth = resume.getBirth();
        number = resume.getNumber();
        email = resume.getEmail();
        address = resume.getAddress();
        gitAddress = resume.getGitAddress();
        blogAddress = resume.getBlogAddress();
        List<Activity> list = resume.getActivities();
        if ( list != null ) {
            activities = new ArrayList<Activity>( list );
        }
        List<Course> list1 = resume.getCourses();
        if ( list1 != null ) {
            courses = new ArrayList<Course>( list1 );
        }
        List<Experience> list2 = resume.getExperiences();
        if ( list2 != null ) {
            experiences = new ArrayList<Experience>( list2 );
        }
        List<Education> list3 = resume.getEducations();
        if ( list3 != null ) {
            educations = new ArrayList<Education>( list3 );
        }
        List<License> list4 = resume.getLicenses();
        if ( list4 != null ) {
            licenses = new ArrayList<License>( list4 );
        }
        List<Language> list5 = resume.getLanguages();
        if ( list5 != null ) {
            languages = new ArrayList<Language>( list5 );
        }

        ResumeResponse resumeResponse = new ResumeResponse( name, birth, number, email, address, gitAddress, blogAddress, activities, courses, experiences, educations, licenses, languages );

        return resumeResponse;
    }
}
