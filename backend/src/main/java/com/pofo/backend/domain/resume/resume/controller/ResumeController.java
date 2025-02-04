package com.pofo.backend.domain.resume.resume.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.resume.resume.dto.request.ResumeCreateRequest;
import com.pofo.backend.domain.resume.resume.dto.response.ResumeCreateResponse;
import com.pofo.backend.domain.resume.resume.dto.response.ResumeIdResponse;
import com.pofo.backend.domain.resume.resume.dto.response.ResumeResponse;
import com.pofo.backend.domain.resume.resume.service.ResumeService;

import com.pofo.backend.domain.user.join.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/resume")
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping("")
    public ResponseEntity<RsData<ResumeIdResponse>> createResume(
        @Valid @RequestBody ResumeCreateRequest resumeCreateRequest,
        @AuthenticationPrincipal User user) {

        ResumeCreateResponse response = resumeService.createResume(resumeCreateRequest, user);
        return ResponseEntity.ok(
            new RsData<>("200", response.getMessage(), new ResumeIdResponse(response.getId())));
    }

    @PutMapping("/{resumeId}")
    public ResponseEntity<RsData<ResumeIdResponse>> updateResume(@PathVariable Long resumeId,
        @RequestBody ResumeCreateRequest resumeCreateRequest, @AuthenticationPrincipal User user) {

        ResumeCreateResponse response = resumeService.updateResume(resumeId, resumeCreateRequest,
            user);
        return ResponseEntity.ok(
            new RsData<>("200", response.getMessage(), new ResumeIdResponse(response.getId())));
    }

    @DeleteMapping("/{resumeId}")
    public ResponseEntity<RsData<Object>> deleteResume(@PathVariable Long resumeId,
        @AuthenticationPrincipal User user) {
        resumeService.deleteResume(resumeId, user);
        return ResponseEntity.ok(new RsData<>("200", "이력서 삭제가 완료되었습니다."));
    }

    @GetMapping("")
    public ResponseEntity<RsData<ResumeResponse>> getResume(@AuthenticationPrincipal User user) {
        ResumeResponse resumeResponse = resumeService.getResumeByUser(user);
        return ResponseEntity.ok(new RsData<>("200", "이력서 조회 성공", resumeResponse));
    }
}
