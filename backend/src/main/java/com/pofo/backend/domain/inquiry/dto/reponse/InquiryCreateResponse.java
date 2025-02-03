package com.pofo.backend.domain.inquiry.dto.reponse;

import com.pofo.backend.domain.inquiry.entity.Inquiry;
import lombok.Getter;

@Getter
public class InquiryCreateResponse {

    private Long responseId;

    public InquiryCreateResponse(Inquiry inquiry) {
        this.responseId = inquiry.getId();
    }
}
