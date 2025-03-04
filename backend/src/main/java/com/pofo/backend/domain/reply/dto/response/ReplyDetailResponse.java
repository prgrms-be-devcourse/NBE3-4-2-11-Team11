package com.pofo.backend.domain.reply.dto.response;

import com.pofo.backend.domain.reply.entity.Reply;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReplyDetailResponse {

    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private final String type = "reply";

    public static ReplyDetailResponse from(Reply reply) {
        return new ReplyDetailResponse(reply.getId(), reply.getContent(), reply.getCreatedAt());
    }
}
