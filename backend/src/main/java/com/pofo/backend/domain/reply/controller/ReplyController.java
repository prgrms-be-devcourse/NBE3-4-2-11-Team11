package com.pofo.backend.domain.reply.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.common.security.AdminDetails;
import com.pofo.backend.domain.admin.login.entitiy.Admin;
import com.pofo.backend.domain.reply.dto.request.ReplyCreateRequest;
import com.pofo.backend.domain.reply.dto.request.ReplyUpdateRequest;
import com.pofo.backend.domain.reply.dto.response.ReplyCreateResponse;
import com.pofo.backend.domain.reply.dto.response.ReplyDetailResponse;
import com.pofo.backend.domain.reply.dto.response.ReplyUpdateResponse;
import com.pofo.backend.domain.reply.service.ReplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/admin/inquiries/{inquiryId}/reply")
    public ResponseEntity<RsData<ReplyCreateResponse>> createReply(@PathVariable Long inquiryId, @Valid @RequestBody ReplyCreateRequest replyCreateRequest, @AuthenticationPrincipal AdminDetails adminDetails) {
        Admin admin = adminDetails.getAdmin();
        ReplyCreateResponse replyCreateResponse = this.replyService.create(inquiryId, replyCreateRequest, admin);
        return ResponseEntity.ok(new RsData<>("200", "답변 생성이 완료되었습니다.", replyCreateResponse));
    }

    @PatchMapping("/admin/inquiries/{inquiryId}/reply/{replyId}")
    public ResponseEntity<RsData<ReplyUpdateResponse>> updateReply(@PathVariable Long inquiryId, @PathVariable Long replyId, @Valid @RequestBody ReplyUpdateRequest replyUpdateRequest, @AuthenticationPrincipal AdminDetails adminDetails) {
        Admin admin = adminDetails.getAdmin();
        ReplyUpdateResponse replyUpdateResponse = this.replyService.update(inquiryId, replyId, replyUpdateRequest, admin);
        return ResponseEntity.ok(new RsData<>("200", "답변 수정이 완료되었습니다.", replyUpdateResponse));
    }

    @DeleteMapping("/admin/inquiries/{inquiryId}/reply/{replyId}")
    public ResponseEntity<RsData<Void>> deleteReply(@PathVariable Long inquiryId, @PathVariable Long replyId, @AuthenticationPrincipal AdminDetails adminDetails) {
        Admin admin = adminDetails.getAdmin();
        this.replyService.delete(inquiryId, replyId, admin);
        return ResponseEntity.ok(new RsData<>("200", "답변 삭제가 완료되었습니다."));
    }

    @GetMapping("/common/inquiries/{inquiryId}/reply")
    public ResponseEntity<RsData<List<ReplyDetailResponse>>> getReply(@PathVariable Long inquiryId) {
        List<ReplyDetailResponse> replyDetailResponses = this.replyService.findByInquiryId(inquiryId);
        return ResponseEntity.ok(new RsData<>("200", "답변 조회가 완료되었습니다.", replyDetailResponses));
    }
}
