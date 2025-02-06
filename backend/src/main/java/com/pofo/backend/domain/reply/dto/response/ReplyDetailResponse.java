package com.pofo.backend.domain.reply.dto.response;

import com.pofo.backend.domain.reply.entity.Reply;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReplyDetailResponse {

    private Long id;
    private String content;

    public static ReplyDetailResponse from(Reply reply) {
        return new ReplyDetailResponse(reply.getId(), reply.getContent());
    }
}
