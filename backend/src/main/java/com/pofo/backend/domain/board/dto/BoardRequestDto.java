package com.pofo.backend.domain.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoardRequestDto {

    @NotNull(message = "사용자 ID는 필수 입력값입니다.") //  user ID 필수 입력
    private Long id;

    @NotBlank(message = "제목은 비어 있을 수 없습니다.") //  제목 필수 입력
    private String title;

    @NotBlank(message = "내용은 비어 있을 수 없습니다.") //  내용 필수 입력
    private String content;
}
