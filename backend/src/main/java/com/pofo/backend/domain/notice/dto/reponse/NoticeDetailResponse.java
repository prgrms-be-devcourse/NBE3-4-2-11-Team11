package com.pofo.backend.domain.notice.dto.reponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.pofo.backend.domain.notice.entity.Notice;

import lombok.Getter;

@Getter
public class NoticeDetailResponse {

    private Long responseId;
    private String subject;
    private String content;
    private LocalDateTime createdAt;

    public NoticeDetailResponse(Notice notice) {
        this.responseId = notice.getId();
        this.subject = notice.getSubject();
        this.content = notice.getContent();
        this.createdAt = notice.getCreatedAt();
    }
}
