package com.pofo.backend.domain.inquiry.dto.reponse;

import com.pofo.backend.domain.inquiry.entity.Inquiry;
import lombok.Getter;

@Getter
public class InquiryUpdateResponse {

    private Long responseId;

    public InquiryUpdateResponse(Inquiry inquiry) {
        this.responseId = inquiry.getId();
    }
}
