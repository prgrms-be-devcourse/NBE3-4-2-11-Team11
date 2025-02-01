package com.pofo.backend.domain.notice.dto;

import com.pofo.backend.domain.notice.entity.Notice;
import lombok.*;

@Getter
public class NoticeResponseDto {
    private Long responseId;

    public NoticeResponseDto(Notice notice) {
        this.responseId = notice.getId();
    }
}
