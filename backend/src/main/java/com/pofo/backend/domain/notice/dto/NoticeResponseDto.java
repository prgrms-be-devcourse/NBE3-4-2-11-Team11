package com.pofo.backend.domain.notice.dto;

import java.time.LocalDateTime;

import com.pofo.backend.domain.notice.entity.Notice;
import lombok.*;

@Getter
public class NoticeResponseDto {
    private Long id;
    private String subject;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public NoticeResponseDto(Notice notice) {
        this.id = notice.getId();
        this.subject = notice.getSubject();
        this.content = notice.getContent();
        this.createdAt = notice.getCreatedAt();
        this.updatedAt = notice.getUpdatedAt();
    }
}
