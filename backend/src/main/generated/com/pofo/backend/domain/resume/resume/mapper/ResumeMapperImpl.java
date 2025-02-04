package com.pofo.backend.domain.resume.resume.mapper;

import com.pofo.backend.domain.resume.language.entity.Language;
import com.pofo.backend.domain.resume.resume.dto.response.ResumeResponse;
import com.pofo.backend.domain.resume.resume.entity.Resume;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-04T11:47:06+0900",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 21.0.2 (GraalVM Community)"
)
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
        List<Language> languages = null;

        name = resume.getName();
        birth = resume.getBirth();
        number = resume.getNumber();
        email = resume.getEmail();
        address = resume.getAddress();
        gitAddress = resume.getGitAddress();
        blogAddress = resume.getBlogAddress();
        List<Language> list = resume.getLanguages();
        if ( list != null ) {
            languages = new ArrayList<Language>( list );
        }

        ResumeResponse resumeResponse = new ResumeResponse( name, birth, number, email, address, gitAddress, blogAddress, languages );

        return resumeResponse;
    }
}
