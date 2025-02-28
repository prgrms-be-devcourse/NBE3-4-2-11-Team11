package com.pofo.backend.domain.board.dto;

import com.pofo.backend.domain.board.entity.Board;
import lombok.Getter;

import java.time.LocalDateTime;


//게시글 상세 조회 -> GET /api/v1/user/boards/{id}

@Getter
public class BoardResponseDto  {
    private final Long id;  //게시글 ID
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;  // 작성일자 필드 추가
    private final String email;  // 작성자 이메일 추가


    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.createdAt = board.getCreatedAt();  // BaseTime의 필드 값 설정
        this.email = board.getUser().getEmail();  // 작성자 이메일 설정
    }
}