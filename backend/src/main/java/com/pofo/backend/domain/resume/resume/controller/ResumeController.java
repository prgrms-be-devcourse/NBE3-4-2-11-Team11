package com.pofo.backend.domain.resume.resume.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.resume.resume.dto.request.ResumeCreateRequest;
import com.pofo.backend.domain.resume.resume.dto.response.ResumeCreateResponse;
import com.pofo.backend.domain.resume.resume.dto.response.ResumeResponse;
import com.pofo.backend.domain.resume.resume.service.ResumeService;
import com.pofo.backend.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/resume")
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping()
    public RsData<Long> createResume(@Valid @RequestBody ResumeCreateRequest resumeCreateRequest, @AuthenticationPrincipal User user) {
        ResumeCreateResponse resumeCreateResponse = resumeService.createResume(resumeCreateRequest, user);
        return new RsData<>("200", resumeCreateResponse.getMessage(), resumeCreateResponse.getId());
    }

    @GetMapping("/resume")
    public ResumeResponse getResume(@AuthenticationPrincipal User user) {
        return resumeService.getResumeByUser(user);
    }
}
