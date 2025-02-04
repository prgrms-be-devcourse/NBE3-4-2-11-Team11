package com.pofo.backend.domain.inquiry.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class InquiryDetailResponse {

    private Long responseId;
    private String subject;
    private String content;
//    private Long userId;
    private LocalDateTime createdAt;

}
