package com.pofo.backend.domain.user.join.entity;

import com.pofo.backend.common.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "oauths")
public class Oauth extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NonNull
    private User user;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "provider 값이 없습니다. ")
    private Provider provider;

    @NotNull(message = "identify 값이 필요합니다.")
    private String identify;

    @CreatedDate
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime createdAt;

    @Getter
    @AllArgsConstructor
    public enum Provider {
        GOOGLE,
        KAKAO,
        NAVER

    }

}
