package com.pofo.backend.domain.resume.resume.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.resume.resume.dto.response.ResumeResponse;
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException;
import com.pofo.backend.domain.resume.resume.service.ResumeService;
import com.pofo.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/resume")
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping()
    public ResponseEntity<RsData<Object>> handleResumeCreationException(ResumeCreationException e) {
        RsData<Object> rsData = new RsData<>("500", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(rsData);
    }

    @GetMapping("/resume")
    public ResumeResponse getResume(@AuthenticationPrincipal User user) {
        return resumeService.getResumeByUser(user);
    }
}
