package com.pofo.backend.domain.resume.resume.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.common.security.CustomUserDetails;
import com.pofo.backend.domain.resume.resume.dto.request.ResumeCreateRequest;
import com.pofo.backend.domain.resume.resume.dto.response.ResumeIdResponse;
import com.pofo.backend.domain.resume.resume.dto.response.ResumeResponse;
import com.pofo.backend.domain.resume.resume.entity.Resume;
import com.pofo.backend.domain.resume.resume.mapper.ResumeMapper;
import com.pofo.backend.domain.resume.resume.service.ResumeService;
import com.pofo.backend.domain.user.join.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/resume")
public class ResumeController {

    private final ResumeService resumeService;
    private final ResumeMapper resumeMapper;

    @PostMapping("")
    public ResponseEntity<RsData<ResumeIdResponse>> createResume(
        @Valid @RequestBody ResumeCreateRequest resumeCreateRequest,
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();
        Resume resume = resumeService.createResume(resumeCreateRequest, user);
        ResumeIdResponse response = new ResumeIdResponse(resume.getId());
        return ResponseEntity.ok(new RsData<>("200", "이력서 생성이 완료되었습니다.", response));
    }

    @PutMapping("/{resumeId}")
    public ResponseEntity<RsData<ResumeIdResponse>> updateResume(@PathVariable Long resumeId,
        @Valid @RequestBody ResumeCreateRequest resumeCreateRequest,
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();
        Resume resume = resumeService.updateResume(resumeId, resumeCreateRequest, user);
        ResumeIdResponse response = new ResumeIdResponse(resume.getId());
        return ResponseEntity.ok(new RsData<>("200", "이력서 수정이 완료되었습니다.", response));
    }

    @DeleteMapping("/{resumeId}")
    public ResponseEntity<RsData<Object>> deleteResume(@PathVariable Long resumeId,
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();
        resumeService.deleteResume(resumeId, user);
        return ResponseEntity.ok(new RsData<>("200", "이력서 삭제가 완료되었습니다."));
    }

    @GetMapping("")
    public ResponseEntity<RsData<ResumeResponse>> getResume(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = customUserDetails.getUser();
        Resume resume = resumeService.getResumeByUser(user);
        ResumeResponse resumeResponse = resumeMapper.toResponse(resume);
        return ResponseEntity.ok(new RsData<>("200", "이력서 조회 성공", resumeResponse));
    }
}
