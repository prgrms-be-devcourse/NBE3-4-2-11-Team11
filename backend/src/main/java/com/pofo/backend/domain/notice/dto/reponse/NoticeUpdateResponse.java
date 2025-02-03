package com.pofo.backend.domain.notice.dto.reponse;

import com.pofo.backend.domain.notice.entity.Notice;

import lombok.Getter;

@Getter
public class NoticeUpdateResponse {

    private Long responseId;

    public NoticeUpdateResponse(Notice notice) {
        this.responseId = notice.getId();
    }
}
