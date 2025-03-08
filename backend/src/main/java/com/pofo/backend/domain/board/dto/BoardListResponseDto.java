package com.pofo.backend.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

//게시글 목록 조회(페이징, 게시글목록)->GET /api/v1/user/boards

@Getter
@AllArgsConstructor
@NoArgsConstructor // JSON 직렬화/역직렬화 , 기본생성자
// json 요청을 객체로 변환할때 문제가 발생하지 않음


public class BoardListResponseDto {
    private int currentPage;
    private int totalPages;
    private long totalItems;
    private List<BoardResponseDto> boards;
    //게시글 목록 (List<BoardResponseDto> 형태로 개별 게시글 정보 포함)
}
