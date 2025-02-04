package com.pofo.backend.domain.resume.resume.mapper;

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
    date = "2025-02-04T17:02:23+0900",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 21.0.2 (GraalVM Community)"
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
        List<Course> list = resume.getCourses();
        if ( list != null ) {
            courses = new ArrayList<Course>( list );
        }
        List<Experience> list1 = resume.getExperiences();
        if ( list1 != null ) {
            experiences = new ArrayList<Experience>( list1 );
        }
        List<Education> list2 = resume.getEducations();
        if ( list2 != null ) {
            educations = new ArrayList<Education>( list2 );
        }
        List<License> list3 = resume.getLicenses();
        if ( list3 != null ) {
            licenses = new ArrayList<License>( list3 );
        }
        List<Language> list4 = resume.getLanguages();
        if ( list4 != null ) {
            languages = new ArrayList<Language>( list4 );
        }

        ResumeResponse resumeResponse = new ResumeResponse( name, birth, number, email, address, gitAddress, blogAddress, courses, experiences, educations, licenses, languages );

        return resumeResponse;
    }
}
