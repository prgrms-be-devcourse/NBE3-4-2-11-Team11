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
    private final String nickname;   // 작성자 닉네임 추가


    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.createdAt = board.getCreatedAt();  // BaseTime의 필드 값 설정
        this.nickname = board.getUser().getNickname();  // 작성자 닉네임 설정
    }
}