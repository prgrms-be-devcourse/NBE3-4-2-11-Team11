package com.pofo.backend.domain.admin.userstats.dto;


import com.pofo.backend.domain.user.join.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatsDto {
    private Long id;
    private String email;
    private String name;
    private User.Sex sex;
    private String nickname;
    private LocalDate age;          // 생년월일 또는 나이를 의미 (필요에 따라 수정)
    private LocalDateTime createdAt;  // 회원가입일 (엔티티 생성일)

    // 편의상 User 엔티티에서 DTO로 변환하는 생성자
    public UserStatsDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.sex = user.getSex();
        this.nickname = user.getNickname();
        this.age = user.getAge();
        this.createdAt = user.getCreatedAt();
    }
}
