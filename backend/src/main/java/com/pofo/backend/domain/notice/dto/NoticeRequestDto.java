package com.pofo.backend.domain.notice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class NoticeRequestDto {
    private Long id;
    private String subject;
    private String content;
}
