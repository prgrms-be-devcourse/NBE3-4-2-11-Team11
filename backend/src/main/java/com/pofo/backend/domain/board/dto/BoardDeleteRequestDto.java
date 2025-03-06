package com.pofo.backend.domain.board.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoardDeleteRequestDto {

    @NotNull(message = "유저 ID는 필수 입력값입니다.") // 유효성 검사 추가
    private Long userId;
}
