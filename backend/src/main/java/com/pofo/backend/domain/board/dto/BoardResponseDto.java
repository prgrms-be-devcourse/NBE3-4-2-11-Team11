package com.pofo.backend.domain.board.dto;

import com.pofo.backend.domain.board.entity.Board;
import lombok.Getter;

@Getter
public class BoardResponseDto {
    private Long id;
    private String title;
    private String content;
    private String author; // 작성자 닉네임
//    this.author = board.getUser().getName(); // 닉네임이 없으면 이름 사용

    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
//        this.author = board.getUser().getNickname();
        this.author = board.getUser() != null ? board.getUser().getNickname() : "알 수 없음";
        //author를 null 아닌 기본값 알수없음으로 설정 ->프론트에서 예외처리필요없음.
    }
    }



