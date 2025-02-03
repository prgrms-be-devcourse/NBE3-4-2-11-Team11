package com.pofo.backend.domain.resume.resume.mapper;

import com.pofo.backend.domain.resume.resume.dto.response.ResumeResponse;
import com.pofo.backend.domain.resume.resume.entity.Resume;
import org.mapstruct.Mapper;

@Mapper
public interface ResumeMapper {
    ResumeResponse resumeToResumeResponse(Resume resume);
}