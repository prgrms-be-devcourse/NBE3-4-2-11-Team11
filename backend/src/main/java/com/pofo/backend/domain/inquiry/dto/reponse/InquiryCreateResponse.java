package com.pofo.backend.domain.inquiry.dto.reponse;

import com.pofo.backend.domain.notice.entity.Notice;
import lombok.Getter;

@Getter
public class InquiryCreateResponse {

    private Long responseId;

    public InquiryCreateResponse(Notice notice) {
        this.responseId = notice.getId();
    }
}
