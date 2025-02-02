package com.pofo.backend.domain.resume.service;


import com.pofo.backend.domain.resume.dto.ResumeRequest;
import com.pofo.backend.domain.resume.dto.ResumeResponse;
import com.pofo.backend.domain.resume.entity.Resume;
import com.pofo.backend.domain.user.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

@Service

public class ResumeService {

    @Transactional
    public ResumeResponse createResume(ResumeRequest resumeRequest, @AuthenticationPrincipal User user) {

        Resume resume = Resume.builder()
            .user(user)
            .name(resumeRequest.getName())
            .birth(resumeRequest.getBirth())
            .number(resumeRequest.getNumber())
            .email(resumeRequest.getEmail())
            .address(resumeRequest.getAddress())
            .gitAddress(resumeRequest.getGitAddress())
            .blogAddress(resumeRequest.getBlogAddress())
            .build();

        return new ResumeResponse(resume.getId(), "이력서 생성이 완료되었습니다.");
    }
}
