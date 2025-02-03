package com.pofo.backend.domain.resume.resume.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.resume.resume.dto.request.ResumeCreateRequest;
import com.pofo.backend.domain.resume.resume.dto.response.ResumeCreateResponse;
import com.pofo.backend.domain.resume.resume.dto.response.ResumeIdResponse;
import com.pofo.backend.domain.resume.resume.dto.response.ResumeResponse;
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException;
import com.pofo.backend.domain.resume.resume.service.ResumeService;
import com.pofo.backend.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<RsData<ResumeIdResponse>> createResume(@Valid @RequestBody ResumeCreateRequest resumeCreateRequest, @AuthenticationPrincipal User user) {
        ResumeCreateResponse resumeCreateResponse = resumeService.createResume(resumeCreateRequest, user);
        ResumeIdResponse resumeIdResponse = new ResumeIdResponse(resumeCreateResponse.getId());
        return ResponseEntity.ok(new RsData<>("200", resumeCreateResponse.getMessage(),resumeIdResponse));
    }

    @PutMapping("/resume/{resumeId}")
    public ResponseEntity<RsData<Object>> updateResume(
        @PathVariable Long resumeId,
        @RequestBody ResumeCreateRequest resumeCreateRequest,
        @AuthenticationPrincipal User user) {
        try {
            ResumeCreateResponse response = resumeService.updateResume(resumeId, resumeCreateRequest, user);
            ResumeIdResponse resumeIdResponse = new ResumeIdResponse(response.getId());
            return ResponseEntity.ok(new RsData<>("200", response.getMessage(),resumeIdResponse));
        } catch (ResumeCreationException e) {
            RsData<Object> rsData = new RsData<>("500", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(rsData);
        }
    }

    @DeleteMapping("/resume/{resumeId}")
    public ResponseEntity<RsData<Object>> deleteResume(
        @PathVariable Long resumeId,
        @AuthenticationPrincipal User user) {
        try {
            resumeService.deleteResume(resumeId, user);
            RsData<Object> rsData = new RsData<>("200", "이력서 삭제가 완료되었습니다.");
            return ResponseEntity.ok(rsData);
        } catch (ResumeCreationException e) {
            RsData<Object> rsData = new RsData<>("500", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(rsData);
        }
    }


    @GetMapping("")
    public ResumeResponse getResume(@AuthenticationPrincipal User user) {
        return resumeService.getResumeByUser(user);
    }
}
