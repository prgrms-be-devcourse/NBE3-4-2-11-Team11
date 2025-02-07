package com.pofo.backend.domain.board.dto;

import com.pofo.backend.domain.board.entity.Board;
import lombok.Getter;


//특정 게시글 조회 -> GET /api/v1/user/boards/{id}

@Getter
public class BoardResponseDto {
    private final Long id;  //게시글 ID
    private final String title;
    private final String content;
    private final String nickname; // 작성자 닉네임


    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
//        this.author = board.getUser().getNickname();
        this.nickname = board.getUser() .getNickname();
    }
    }



