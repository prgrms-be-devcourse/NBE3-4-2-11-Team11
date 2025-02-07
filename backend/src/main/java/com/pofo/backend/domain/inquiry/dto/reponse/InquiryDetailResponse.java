package com.pofo.backend.domain.inquiry.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class InquiryDetailResponse {

    //    private Long userId;
    private Long id;
    private String subject;
    private String content;
    private int response;
    private LocalDateTime createdAt;

}
