package com.pofo.backend.domain.board.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
//import lombok.Setter;

@Getter
//@Setter  // dto는 주로 불변 객체로 사용하는게 좋음
@AllArgsConstructor // 모든 필드를 포함하는 생성자 자동 생성
@NoArgsConstructor

//게시글 생성, 수정시 사용되는 dto
// POST /api/v1/user/boards -> 게시글 생성
//PATCH /api/v1/user/boards/{id} → 게시글 수정

public class BoardRequestDto {

    @NotNull(message = "제목은 비어 있을 수 없습니다.") //  제목 필수 입력
    private String title;

    @NotNull(message = "내용은 비어 있을 수 없습니다.") //  내용 필수 입력
    private String content;

//    @NotBlank(message = "이메일은 필수 입력값입니다.")
//    private String email;
}
