package com.pofo.backend.domain.reply.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.reply.dto.request.ReplyCreateRequest;
import com.pofo.backend.domain.reply.dto.request.ReplyUpdateRequest;
import com.pofo.backend.domain.reply.dto.response.ReplyCreateResponse;
import com.pofo.backend.domain.reply.dto.response.ReplyUpdateResponse;
import com.pofo.backend.domain.reply.service.ReplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/inquiries")
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("{id}/reply")
    public ResponseEntity<RsData<ReplyCreateResponse>> createReply(@PathVariable Long id, @Valid @RequestBody ReplyCreateRequest replyCreateRequest, @AuthenticationPrincipal Admin admin) {
        ReplyCreateResponse replyCreateResponse = this.replyService.create(id, replyCreateRequest, admin);
        return ResponseEntity.ok(new RsData<>("200", "답변 생성이 완료되었습니다.", replyCreateResponse));
    }

    @PatchMapping("{inquiryId}/reply/{replyId}")
    public ResponseEntity<RsData<ReplyUpdateResponse>> updateReply(@PathVariable Long inquiryId, @PathVariable Long replyId, @Valid @RequestBody ReplyUpdateRequest replyUpdateRequest, @AuthenticationPrincipal Admin admin) {
        ReplyUpdateResponse replyUpdateResponse = this.replyService.update(inquiryId, replyId, replyUpdateRequest, admin);
        return ResponseEntity.ok(new RsData<>("200", "답변 수정이 완료되었습니다.", replyUpdateResponse));
    }

    @DeleteMapping("{inquiryId}/reply/{replyId}")
    public ResponseEntity<RsData<Void>> deleteReply(@PathVariable Long inquiryId, @PathVariable Long replyId) {
        this.replyService.delete(inquiryId, replyId, admin);
        return ResponseEntity.ok(new RsData<>("200", "답변 삭제가 완료되었습니다."));
    }


}
