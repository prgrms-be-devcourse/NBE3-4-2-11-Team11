package com.pofo.backend.domain.resume.service;


import com.pofo.backend.domain.resume.dto.request.ResumeCreateRequest;
import com.pofo.backend.domain.resume.dto.response.ResumeCreateResponse;
import com.pofo.backend.domain.resume.entity.Resume;
import com.pofo.backend.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class ResumeService {

    @Transactional
    public ResumeCreateResponse createResume(ResumeCreateRequest resumeCreateRequest, @AuthenticationPrincipal User user) {

        Resume resume = Resume.builder()
            .user(user)
            .name(resumeCreateRequest.getName())
            .birth(resumeCreateRequest.getBirth())
            .number(resumeCreateRequest.getNumber())
            .email(resumeCreateRequest.getEmail())
            .address(resumeCreateRequest.getAddress())
            .gitAddress(resumeCreateRequest.getGitAddress())
            .blogAddress(resumeCreateRequest.getBlogAddress())
            .build();

        return new ResumeCreateResponse(resume.getId(), "이력서 생성이 완료되었습니다.");

    }
}
