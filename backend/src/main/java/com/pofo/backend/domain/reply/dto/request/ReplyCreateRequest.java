package com.pofo.backend.domain.reply.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReplyCreateRequest {

    @NotBlank(message = "내용은 필수 항목입니다.")
    private String content;
}
