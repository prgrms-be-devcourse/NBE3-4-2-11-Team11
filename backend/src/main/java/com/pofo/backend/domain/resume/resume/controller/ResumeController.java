package com.pofo.backend.domain.resume.resume.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.common.security.CustomUserDetails;
import com.pofo.backend.domain.resume.resume.dto.request.ResumeCreateRequest;
import com.pofo.backend.domain.resume.resume.dto.response.ResumeResponse;
import com.pofo.backend.domain.resume.resume.service.ResumeService;
import com.pofo.backend.domain.user.join.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<RsData<Object>> createResume(
        @Valid @RequestBody ResumeCreateRequest resumeCreateRequest,
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();
        resumeService.createResume(resumeCreateRequest, user);
        return ResponseEntity.ok(new RsData<>("200", "이력서 생성이 완료되었습니다."));
    }

    @PutMapping("")
    public ResponseEntity<RsData<Object>> updateResume(
        @Valid @RequestBody ResumeCreateRequest resumeCreateRequest,
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();
         resumeService.updateResume(resumeCreateRequest, user);
        return ResponseEntity.ok(new RsData<>("200", "이력서 수정이 완료되었습니다."));
    }

    @DeleteMapping("")
    public ResponseEntity<RsData<Object>> deleteResume(
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();
        resumeService.deleteResume(user);
        return ResponseEntity.ok(new RsData<>("200", "이력서 삭제가 완료되었습니다."));
    }

    @GetMapping("")
    public ResponseEntity<RsData<ResumeResponse>> getResume(
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = customUserDetails.getUser();
        ResumeResponse resumeResponse = resumeService.getResumeResponse(user);

        return ResponseEntity.ok(new RsData<>("200", "이력서 조회 성공", resumeResponse));
    }


}
