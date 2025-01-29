package com.pofo.backend.domain.user.join.entity;

import com.pofo.backend.common.jpa.entity.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "oauths")
public class Oauths extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    private String identify;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Getter
    @AllArgsConstructor
    private enum Provider {
        GOOGLE,
        KAKAO,
        NAVER

    }

}
