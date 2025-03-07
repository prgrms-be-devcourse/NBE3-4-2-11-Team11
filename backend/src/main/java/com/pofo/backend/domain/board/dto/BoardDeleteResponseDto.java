package com.pofo.backend.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor

public class BoardDeleteResponseDto {
    private String message = "삭제되었습니다.";
    //기본메시지 설정, 생성자로 값을 전달하지 않아도 기본응답 보낼 수 있음.
}
