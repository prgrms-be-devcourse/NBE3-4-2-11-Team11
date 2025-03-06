package com.pofo.backend.domain.board.entity;

import com.pofo.backend.common.jpa.entity.BaseTime;
import com.pofo.backend.domain.user.join.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "boards")
public class Board extends BaseTime { //base엔티티에 테이블id존재,  상속받음

    @ManyToOne(fetch = FetchType.LAZY) //(, cascade = CascadeType.REMOVE)  유저 삭제 시 해당 유저 게시글 자동 삭제
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false) // FK 컬럼 명시
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false) // 마크다운 저장
    private String content;
}
