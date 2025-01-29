package com.pofo.backend.domain.notice.dto;

import com.pofo.backend.domain.notice.entity.Notice;
import lombok.*;

@Getter
public class NoticeResponseDto {
    private Long id;
    private String subject;
    private String content;

    public NoticeResponseDto(Notice notice) {
        this.id = notice.getId();
        this.subject = notice.getSubject();
        this.content = notice.getContent();
    }
}
