package com.pofo.backend.domain.notice.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class NoticeDetailResponse {

    private Long id;
    private String subject;
    private String content;
    private LocalDateTime createdAt;
}
