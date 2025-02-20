package com.pofo.backend.domain.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentDetailResponse {

    private Long id;
    private String content;
}
