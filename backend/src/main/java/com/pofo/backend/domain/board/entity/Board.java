package com.pofo.backend.domain.board.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.pofo.backend.domain.user.entity.Users; // Users 엔티티 임포트

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  ManyToOne 관계 설정 (users 테이블의 id를 참조)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false) // FK 컬럼 명시
    private Users user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false) //마크다운 저장
    private String content;

//    @Column(nullable = true)
//    private String imageUrl;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;


    //INSERT  새로운 엔티티 저장 전에 실행
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();  //최초 생성시간
        this.updatedAt = LocalDateTime.now(); // 업데이트 시간
    }
    //UPDATE 기존 엔티티 수정 전에 실행
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now(); //업데이트 시간
    }
}