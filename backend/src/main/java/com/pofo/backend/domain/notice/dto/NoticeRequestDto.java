package com.pofo.backend.domain.notice.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeRequestDto {
    private Long id;
    private String subject;
    private String content;
}
