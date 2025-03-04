package com.pofo.backend.domain.comment.dto.response;

import com.pofo.backend.domain.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentDetailResponse {

    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private final String type = "comment";

    public static CommentDetailResponse from(Comment comment) {
        return new CommentDetailResponse(comment.getId(), comment.getContent(), comment.getCreatedAt());
    }
}
