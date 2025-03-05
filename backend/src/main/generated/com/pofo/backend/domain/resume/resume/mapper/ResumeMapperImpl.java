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
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-04T15:42:56+0900",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 21.0.5 (Oracle Corporation)"
)
@Component
public class ResumeMapperImpl implements ResumeMapper {

    @Override
    public ResumeResponse resumeToResumeResponse(Resume resume) {
        if ( resume == null ) {
            return null;
        }

        ResumeResponse.ResumeResponseBuilder resumeResponse = ResumeResponse.builder();

        resumeResponse.name( resume.getName() );
        resumeResponse.birth( resume.getBirth() );
        resumeResponse.number( resume.getNumber() );
        resumeResponse.email( resume.getEmail() );
        resumeResponse.address( resume.getAddress() );
        resumeResponse.addressDetail( resume.getAddressDetail() );
        resumeResponse.gitAddress( resume.getGitAddress() );
        resumeResponse.blogAddress( resume.getBlogAddress() );
        resumeResponse.activities( activitySetToActivityResponseSet( resume.getActivities() ) );
        resumeResponse.courses( courseSetToCourseResponseSet( resume.getCourses() ) );
        resumeResponse.experiences( experienceSetToExperienceResponseSet( resume.getExperiences() ) );
        resumeResponse.educations( educationSetToEducationResponseSet( resume.getEducations() ) );
        resumeResponse.licenses( licenseSetToLicenseResponseSet( resume.getLicenses() ) );
        resumeResponse.languages( languageSetToLanguageResponseSet( resume.getLanguages() ) );

        resumeResponse.skills( mapSkills(resume) );
        resumeResponse.tools( mapTools(resume) );

        return resumeResponse.build();
    }

    @Override
    public ActivityResponse activityToActivityResponse(Activity activity) {
        if ( activity == null ) {
            return null;
        }

        ActivityResponse.ActivityResponseBuilder activityResponse = ActivityResponse.builder();

        activityResponse.name( activity.getName() );
        activityResponse.history( activity.getHistory() );
        activityResponse.startDate( activity.getStartDate() );
        activityResponse.endDate( activity.getEndDate() );
        activityResponse.awards( awardSetToAwardResponseSet( activity.getAwards() ) );

        return activityResponse.build();
    }

    @Override
    public CourseResponse courseToCourseResponse(Course course) {
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
    public ExperienceResponse experienceToExperienceResponse(Experience experience) {
        if ( experience == null ) {
            return null;
        }

        ExperienceResponse.ExperienceResponseBuilder experienceResponse = ExperienceResponse.builder();

        experienceResponse.name( experience.getName() );
        experienceResponse.department( experience.getDepartment() );
        experienceResponse.position( experience.getPosition() );
        experienceResponse.responsibility( experience.getResponsibility() );
        experienceResponse.startDate( experience.getStartDate() );
        experienceResponse.endDate( experience.getEndDate() );

        return experienceResponse.build();
    }

    @Override
    public EducationResponse educationToEducationResponse(Education education) {
        if ( education == null ) {
            return null;
        }

        EducationResponse.EducationResponseBuilder educationResponse = EducationResponse.builder();

        educationResponse.name( education.getName() );
        educationResponse.major( education.getMajor() );
        educationResponse.startDate( education.getStartDate() );
        educationResponse.endDate( education.getEndDate() );
        educationResponse.status( education.getStatus() );

        return educationResponse.build();
    }

    @Override
    public LicenseResponse licenseToLicenseResponse(License license) {
        if ( license == null ) {
            return null;
        }

        LicenseResponse.LicenseResponseBuilder licenseResponse = LicenseResponse.builder();

        licenseResponse.name( license.getName() );
        licenseResponse.institution( license.getInstitution() );
        licenseResponse.certifiedDate( license.getCertifiedDate() );

        return licenseResponse.build();
    }

    @Override
    public LanguageResponse languageToLanguageResponse(Language language) {
        if ( language == null ) {
            return null;
        }

        LanguageResponse.LanguageResponseBuilder languageResponse = LanguageResponse.builder();

        languageResponse.language( language.getLanguage() );
        languageResponse.name( language.getName() );
        languageResponse.result( language.getResult() );
        languageResponse.certifiedDate( language.getCertifiedDate() );

        return languageResponse.build();
    }

    protected Set<ActivityResponse> activitySetToActivityResponseSet(Set<Activity> set) {
        if ( set == null ) {
            return null;
        }

        Set<ActivityResponse> set1 = new LinkedHashSet<ActivityResponse>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Activity activity : set ) {
            set1.add( activityToActivityResponse( activity ) );
        }

        return set1;
    }

    protected Set<CourseResponse> courseSetToCourseResponseSet(Set<Course> set) {
        if ( set == null ) {
            return null;
        }

        Set<CourseResponse> set1 = new LinkedHashSet<CourseResponse>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Course course : set ) {
            set1.add( courseToCourseResponse( course ) );
        }

        return set1;
    }

    protected Set<ExperienceResponse> experienceSetToExperienceResponseSet(Set<Experience> set) {
        if ( set == null ) {
            return null;
        }

        Set<ExperienceResponse> set1 = new LinkedHashSet<ExperienceResponse>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Experience experience : set ) {
            set1.add( experienceToExperienceResponse( experience ) );
        }

        return set1;
    }

    protected Set<EducationResponse> educationSetToEducationResponseSet(Set<Education> set) {
        if ( set == null ) {
            return null;
        }

        Set<EducationResponse> set1 = new LinkedHashSet<EducationResponse>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Education education : set ) {
            set1.add( educationToEducationResponse( education ) );
        }

        return set1;
    }

    protected Set<LicenseResponse> licenseSetToLicenseResponseSet(Set<License> set) {
        if ( set == null ) {
            return null;
        }

        Set<LicenseResponse> set1 = new LinkedHashSet<LicenseResponse>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( License license : set ) {
            set1.add( licenseToLicenseResponse( license ) );
        }

        return set1;
    }

    protected Set<LanguageResponse> languageSetToLanguageResponseSet(Set<Language> set) {
        if ( set == null ) {
            return null;
        }

        Set<LanguageResponse> set1 = new LinkedHashSet<LanguageResponse>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Language language : set ) {
            set1.add( languageToLanguageResponse( language ) );
        }

        return set1;
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

    protected Set<AwardResponse> awardSetToAwardResponseSet(Set<Award> set) {
        if ( set == null ) {
            return null;
        }

        Set<AwardResponse> set1 = new LinkedHashSet<AwardResponse>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Award award : set ) {
            set1.add( awardToAwardResponse( award ) );
        }

        return set1;
    }
}
