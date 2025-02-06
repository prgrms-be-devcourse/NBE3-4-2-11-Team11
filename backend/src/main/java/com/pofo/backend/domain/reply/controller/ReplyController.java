package com.pofo.backend.domain.reply.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.inquiry.service.InquiryService;
import com.pofo.backend.domain.reply.dto.request.ReplyCreateRequest;
import com.pofo.backend.domain.reply.dto.response.ReplyCreateResponse;
import com.pofo.backend.domain.reply.service.ReplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/inquiries")
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;
    private final InquiryService inquiryService;

    @PostMapping("{id}/reply")
    public ResponseEntity<RsData<ReplyCreateResponse>> createReply(@PathVariable Long id, @Valid @RequestBody ReplyCreateRequest replyCreateRequest) {
        ReplyCreateResponse replyCreateResponse = this.replyService.create(id, replyCreateRequest);
        return ResponseEntity.ok(new RsData<>("200", "문의사항 답변 생성이 완료되었습니다.", replyCreateResponse));

    }
}
