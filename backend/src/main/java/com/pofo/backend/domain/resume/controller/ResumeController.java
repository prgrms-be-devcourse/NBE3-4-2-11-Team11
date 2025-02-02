package com.pofo.backend.domain.resume.controller;

import com.pofo.backend.domain.resume.dto.ResumeRequest;
import com.pofo.backend.domain.resume.dto.ResumeResponse;
import com.pofo.backend.domain.resume.service.ResumeService;
import com.pofo.backend.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResumeResponse createResume(@Valid @RequestBody ResumeRequest resumeRequest,@AuthenticationPrincipal User user) {
        return resumeService.createResume(resumeRequest,user);
    }
}
