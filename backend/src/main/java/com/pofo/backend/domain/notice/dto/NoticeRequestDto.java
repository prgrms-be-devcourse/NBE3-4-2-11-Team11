package com.pofo.backend.domain.notice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class NoticeRequestDto {
    @NotBlank(message = "제목은 필수 항목입니다.")
    private String subject;

    @NotBlank(message = "내용은 필수 항목입니다.")
    private String content;
}
