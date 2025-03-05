
package com.pofo.backend.domain.user.join.entity;

import com.pofo.backend.common.jpa.entity.BaseEntity;
import com.pofo.backend.domain.resume.resume.entity.Resume;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users",
        uniqueConstraints = { @UniqueConstraint(columnNames = "email") })
public class User extends BaseEntity {

    @Column(unique = true)
    @NotNull(message = "email 값이 필요합니다.")
    public String email;

    @NotNull(message = "name 값이 필요합니다.")
    public String name;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "sex 값이 필요합니다.")
    public Sex sex;

    @Setter
    @NotNull(message = "nickname 값이 필요합니다.")
    public String nickname;

    @NotNull(message = "age 값이 필요합니다.")
    public LocalDate age;

    @CreatedDate
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime createdAt;

    // 마지막 로그인 시간
    @Setter
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    // 관심 직종
    @Setter
    @Column(nullable = true)
    private String jobInterest;

    // 취업 상태
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private UserStatus userStatus;

    // 휴먼 계정 여부 ("N" : 일반, "Y" : 휴먼)
    @Getter
    @Setter
    @Column(nullable = false, name = "dormant_flg", columnDefinition = "varchar(1) default 'N'")
    private String dormantFlg = "N";
    // 휴먼 처리 시작 시간
    @Setter
    @Column(name = "dormant_start_at")
    private LocalDateTime dormantStartAt;



    // 휴먼 처리 종료 시간 (해제 시 업데이트)
    @Setter
    @Column(name = "dormant_end_at")
    private LocalDateTime dormantEndAt;

    @Getter
    @AllArgsConstructor
    public enum Sex {
        MALE,
        FEMALE;
    }

    @Getter
    @AllArgsConstructor
    public enum UserStatus {
        UNEMPLOYED,
        EMPLOYED,
        STUDENT;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resume> resumes = new ArrayList<>();
}
