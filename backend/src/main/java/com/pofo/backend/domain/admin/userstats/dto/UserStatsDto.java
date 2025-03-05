package com.pofo.backend.domain.admin.userstats.dto;

import com.pofo.backend.domain.user.join.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatsDto {
    private Long id;
    private String email;
    private String name;
    private User.Sex sex;
    private String nickname;
    private String age;          // 생년월일을 포맷팅한 문자열
    private String createdAt;    // 가입일을 포맷팅한 문자열
    private String lastLoginAt;  // 마지막 로그인 시간 포맷팅한 문자열
    private String jobInterest;
    private User.UserStatus userStatus;
    private String dormantFlg;
    private String dormantStartAt; // 휴먼 처리 시작 시간 포맷팅한 문자열
    private String dormantEndAt;   // 휴먼 처리 종료 시간 포맷팅한 문자열

    // User 엔티티에서 DTO로 변환하는 생성자 (날짜 및 시간은 포맷팅 처리)
    public UserStatsDto(User user) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.sex = user.getSex();
        this.nickname = user.getNickname();
        this.age = user.getAge() != null ? user.getAge().format(dateFormatter) : null;
        this.createdAt = user.getCreatedAt() != null ? user.getCreatedAt().format(dateFormatter) : null;
        this.lastLoginAt = user.getLastLoginAt() != null ? user.getLastLoginAt().format(dateTimeFormatter) : null;
        this.jobInterest = user.getJobInterest();
        this.userStatus = user.getUserStatus();
        this.dormantFlg = user.getDormantFlg();
        this.dormantStartAt = user.getDormantStartAt() != null ? user.getDormantStartAt().format(dateTimeFormatter) : null;
        this.dormantEndAt = user.getDormantEndAt() != null ? user.getDormantEndAt().format(dateTimeFormatter) : null;
    }
}
