package com.pofo.backend.domain.inquiry.dto.reponse;

import com.pofo.backend.domain.inquiry.entity.Inquiry;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class InquiryDetailResponse {

    private Long responseId;
    private String subject;
    private String content;
//    private Long userId;
    private LocalDateTime createdAt;

    public InquiryDetailResponse(Inquiry inquiry) {
        this.responseId = inquiry.getId();
        this.subject = inquiry.getSubject();
        this.content = inquiry.getContent();
//        this.userId = inquiry.getUser().getId();
        this.createdAt = inquiry.getCreatedAt();
    }
}
