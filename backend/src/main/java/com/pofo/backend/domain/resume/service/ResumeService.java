package com.pofo.backend.domain.resume.service;


import com.pofo.backend.domain.resume.exception.ResumeCreationException;
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

        if (user == null) {
            throw new ResumeCreationException("사용자 정보가 존재하지 않습니다.");
        }
        try {
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
        } catch (Exception e) {
            throw new ResumeCreationException("이력서 생성 중 오류가 발생했습니다.");
        }
    }
}
