package com.pofo.backend.domain.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

//게시글 생성, 수정시 사용되는 dto
// POST /api/v1/user/boards -> 게시글 생성
//PATCH /api/v1/user/boards/{id} → 게시글 수정

public class BoardRequestDto {

    @NotBlank(message = "제목은 비어 있을 수 없습니다.") //  제목 필수 입력
    private String title;

    @NotBlank(message = "내용은 비어 있을 수 없습니다.") //  내용 필수 입력
    private String content;

    @NotNull(message = "유저 ID는 필수 입력값입니다.") //
    private long userId;  //null 방지
}
