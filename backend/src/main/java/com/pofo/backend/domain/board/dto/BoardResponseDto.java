package com.pofo.backend.domain.board.dto;
import com.pofo.backend.common.base.BaseInitData;
import com.pofo.backend.domain.board.entity.Board;
import com.pofo.backend.domain.user.join.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;


//게시글 상세 조회 -> GET /api/v1/user/boards/{id}

//
//@Getter
//public class BoardResponseDto  {
//    private final Long id;  //게시글 ID
//    private final String title;
//    private final String content;
//    private final LocalDateTime createdAt;  // 작성일자 필드 추가
//    private final String email;  // 작성자 이메일 추가
//
//
//    public BoardResponseDto(Board board) {
//        this.id = board.getId();
//        this.title = board.getTitle();
//        this.content = board.getContent();
//        this.createdAt = board.getCreatedAt();  // BaseTime의 필드 값 설정
//        this.email = board.getUser().getEmail();  // 작성자 이메일 설정
//    }
//}


@Getter
public class BoardResponseDto  {
    private final Long id;  // 게시글 ID
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;  // 작성일자
    private final UserDto user;  // 작성자 정보 (User 객체 대신 DTO 사용)

    // 🔥 내부 클래스로 User 정보를 포함하는 DTO 생성
    @Getter
    public static class UserDto {
        private final Long id;  // 유저 ID
        private final String email;  // 이메일
        private final String nickname;  // 닉네임

        public UserDto(User user) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.nickname = user.getNickname();  // 필요하면 User 엔티티에 nickname 추가
        }
    }

    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.createdAt = board.getCreatedAt();  // BaseTime의 필드 값 설정
        this.user = new UserDto(board.getUser());  // 🔥 User 정보 포함
    }
}
