package com.pofo.backend.domain.inquiry.dto.reponse;

import com.pofo.backend.domain.inquiry.entity.Inquiry;
import com.pofo.backend.domain.reply.dto.response.ReplyDetailResponse;
import com.pofo.backend.domain.reply.entity.Reply;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class InquiryDetailResponse {

    private Long userId;
    private Long id;
    private String subject;
    private String content;
    private int response;
    private LocalDateTime createdAt;
    private ReplyDetailResponse reply;

    public static InquiryDetailResponse from(Inquiry inquiry, Reply reply) {
        return new InquiryDetailResponse(
                inquiry.getUser().getId(),
                inquiry.getId(),
                inquiry.getSubject(),
                inquiry.getContent(),
                inquiry.getResponse(),
                inquiry.getCreatedAt(),
                reply != null ? ReplyDetailResponse.from(reply) : null // 답변이 있을 경우 DTO로 변환
        );
    }
}
