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
@Table(name = "users")
public class Users extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String name;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    private String nickname;

    private String age;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Getter
    @AllArgsConstructor
    private enum Sex {
        MALE,
        FEMALE;

    }
}
