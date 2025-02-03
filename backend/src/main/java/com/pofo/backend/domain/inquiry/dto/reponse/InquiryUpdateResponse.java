package com.pofo.backend.domain.inquiry.dto.reponse;

import com.pofo.backend.domain.notice.entity.Notice;
import lombok.Getter;

@Getter
public class InquiryUpdateResponse {

    private Long responseId;

    public InquiryUpdateResponse(Notice notice) {
        this.responseId = notice.getId();
    }
}
