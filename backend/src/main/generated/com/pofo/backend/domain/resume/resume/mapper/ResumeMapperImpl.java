package com.pofo.backend.domain.resume.resume.mapper;

import com.pofo.backend.domain.resume.activity.activity.dto.ActivityResponse;
import com.pofo.backend.domain.resume.activity.activity.entity.Activity;
import com.pofo.backend.domain.resume.activity.award.dto.AwardResponse;
import com.pofo.backend.domain.resume.activity.award.entity.Award;
import com.pofo.backend.domain.resume.course.dto.CourseResponse;
import com.pofo.backend.domain.resume.course.entity.Course;
import com.pofo.backend.domain.resume.education.dto.EducationResponse;
import com.pofo.backend.domain.resume.education.entity.Education;
import com.pofo.backend.domain.resume.experience.dto.ExperienceResponse;
import com.pofo.backend.domain.resume.experience.entity.Experience;
import com.pofo.backend.domain.resume.language.dto.LanguageResponse;
import com.pofo.backend.domain.resume.language.entity.Language;
import com.pofo.backend.domain.resume.license.dto.LicenseResponse;
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
    date = "2025-02-08T00:58:53+0900",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 21.0.5 (Oracle Corporation)"
)
@Component
public class ResumeMapperImpl implements ResumeMapper {

    @Override
    public ResumeResponse toResponse(Resume resume) {
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
        List<ActivityResponse> activities = null;
        List<CourseResponse> courses = null;
        List<ExperienceResponse> experiences = null;
        List<EducationResponse> educations = null;
        List<LicenseResponse> licenses = null;
        List<LanguageResponse> languages = null;

        name = resume.getName();
        birth = resume.getBirth();
        number = resume.getNumber();
        email = resume.getEmail();
        address = resume.getAddress();
        gitAddress = resume.getGitAddress();
        blogAddress = resume.getBlogAddress();
        activities = activityListToActivityResponseList( resume.getActivities() );
        courses = courseListToCourseResponseList( resume.getCourses() );
        experiences = experienceListToExperienceResponseList( resume.getExperiences() );
        educations = educationListToEducationResponseList( resume.getEducations() );
        licenses = licenseListToLicenseResponseList( resume.getLicenses() );
        languages = languageListToLanguageResponseList( resume.getLanguages() );

        ResumeResponse resumeResponse = new ResumeResponse( name, birth, number, email, address, gitAddress, blogAddress, activities, courses, experiences, educations, licenses, languages );

        return resumeResponse;
    }

    @Override
    public ActivityResponse toResponse(Activity activity) {
        if ( activity == null ) {
            return null;
        }

        ActivityResponse.ActivityResponseBuilder activityResponse = ActivityResponse.builder();

        activityResponse.name( activity.getName() );
        activityResponse.history( activity.getHistory() );
        activityResponse.startDate( activity.getStartDate() );
        activityResponse.endDate( activity.getEndDate() );
        activityResponse.awards( awardListToAwardResponseList( activity.getAwards() ) );

        return activityResponse.build();
    }

    @Override
    public CourseResponse toResponse(Course course) {
        if ( course == null ) {
            return null;
        }

        CourseResponse.CourseResponseBuilder courseResponse = CourseResponse.builder();

        courseResponse.name( course.getName() );
        courseResponse.institution( course.getInstitution() );
        courseResponse.startDate( course.getStartDate() );
        courseResponse.endDate( course.getEndDate() );

        return courseResponse.build();
    }

    @Override
    public ExperienceResponse toResponse(Experience experience) {
        if ( experience == null ) {
            return null;
        }

        ExperienceResponse experienceResponse = new ExperienceResponse();

        experienceResponse.setName( experience.getName() );
        experienceResponse.setDepartment( experience.getDepartment() );
        experienceResponse.setPosition( experience.getPosition() );
        experienceResponse.setResponsibility( experience.getResponsibility() );
        experienceResponse.setStartDate( experience.getStartDate() );
        experienceResponse.setEndDate( experience.getEndDate() );

        return experienceResponse;
    }

    @Override
    public EducationResponse toResponse(Education education) {
        if ( education == null ) {
            return null;
        }

        EducationResponse educationResponse = new EducationResponse();

        educationResponse.setName( education.getName() );
        educationResponse.setMajor( education.getMajor() );
        educationResponse.setStartDate( education.getStartDate() );
        educationResponse.setEndDate( education.getEndDate() );
        educationResponse.setStatus( education.getStatus() );

        return educationResponse;
    }

    @Override
    public LicenseResponse toResponse(License license) {
        if ( license == null ) {
            return null;
        }

        LicenseResponse licenseResponse = new LicenseResponse();

        licenseResponse.setName( license.getName() );
        licenseResponse.setInstitution( license.getInstitution() );
        licenseResponse.setCertifiedDate( license.getCertifiedDate() );

        return licenseResponse;
    }

    @Override
    public LanguageResponse toResponse(Language language) {
        if ( language == null ) {
            return null;
        }

        LanguageResponse languageResponse = new LanguageResponse();

        languageResponse.setLanguage( language.getLanguage() );
        languageResponse.setName( language.getName() );
        languageResponse.setResult( language.getResult() );
        languageResponse.setCertifiedDate( language.getCertifiedDate() );

        return languageResponse;
    }

    protected List<ActivityResponse> activityListToActivityResponseList(List<Activity> list) {
        if ( list == null ) {
            return null;
        }

        List<ActivityResponse> list1 = new ArrayList<ActivityResponse>( list.size() );
        for ( Activity activity : list ) {
            list1.add( toResponse( activity ) );
        }

        return list1;
    }

    protected List<CourseResponse> courseListToCourseResponseList(List<Course> list) {
        if ( list == null ) {
            return null;
        }

        List<CourseResponse> list1 = new ArrayList<CourseResponse>( list.size() );
        for ( Course course : list ) {
            list1.add( toResponse( course ) );
        }

        return list1;
    }

    protected List<ExperienceResponse> experienceListToExperienceResponseList(List<Experience> list) {
        if ( list == null ) {
            return null;
        }

        List<ExperienceResponse> list1 = new ArrayList<ExperienceResponse>( list.size() );
        for ( Experience experience : list ) {
            list1.add( toResponse( experience ) );
        }

        return list1;
    }

    protected List<EducationResponse> educationListToEducationResponseList(List<Education> list) {
        if ( list == null ) {
            return null;
        }

        List<EducationResponse> list1 = new ArrayList<EducationResponse>( list.size() );
        for ( Education education : list ) {
            list1.add( toResponse( education ) );
        }

        return list1;
    }

    protected List<LicenseResponse> licenseListToLicenseResponseList(List<License> list) {
        if ( list == null ) {
            return null;
        }

        List<LicenseResponse> list1 = new ArrayList<LicenseResponse>( list.size() );
        for ( License license : list ) {
            list1.add( toResponse( license ) );
        }

        return list1;
    }

    protected List<LanguageResponse> languageListToLanguageResponseList(List<Language> list) {
        if ( list == null ) {
            return null;
        }

        List<LanguageResponse> list1 = new ArrayList<LanguageResponse>( list.size() );
        for ( Language language : list ) {
            list1.add( toResponse( language ) );
        }

        return list1;
    }

    protected AwardResponse awardToAwardResponse(Award award) {
        if ( award == null ) {
            return null;
        }

        AwardResponse.AwardResponseBuilder awardResponse = AwardResponse.builder();

        awardResponse.name( award.getName() );
        awardResponse.institution( award.getInstitution() );
        awardResponse.awardDate( award.getAwardDate() );

        return awardResponse.build();
    }

    protected List<AwardResponse> awardListToAwardResponseList(List<Award> list) {
        if ( list == null ) {
            return null;
        }

        List<AwardResponse> list1 = new ArrayList<AwardResponse>( list.size() );
        for ( Award award : list ) {
            list1.add( awardToAwardResponse( award ) );
        }

        return list1;
    }
}
