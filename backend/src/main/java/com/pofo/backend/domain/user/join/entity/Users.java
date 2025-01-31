package com.pofo.backend.domain.user.join.entity;

import com.pofo.backend.common.jpa.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String email;

    public String name;

    @Enumerated(EnumType.STRING)
    public Sex sex;

    public String nickname;

    public LocalDate age;

    @CreatedDate
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime createdAt;

    @Getter
    @AllArgsConstructor
    public enum Sex {
        MALE,
        FEMALE;

    }
}
