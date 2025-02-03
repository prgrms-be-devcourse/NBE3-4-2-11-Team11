package com.pofo.backend.domain.notice.dto.reponse;

import com.pofo.backend.domain.notice.entity.Notice;
import lombok.*;

@Getter
public class NoticeCreateResponse {

    private Long responseId;

    public NoticeCreateResponse(Notice notice) {
        this.responseId = notice.getId();
    }
}
