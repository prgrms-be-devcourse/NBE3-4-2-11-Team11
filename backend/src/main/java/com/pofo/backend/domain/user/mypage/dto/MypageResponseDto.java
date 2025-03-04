package com.pofo.backend.domain.user.mypage.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MypageResponseDto {
    private final String email;
    private final String name;
    private final String nickname;
    private final String sex;
    private final String age; // ✅ YYYY-MM-DD 형태로 변환
    private final String jobInterest;
    private final String userStatus;
}
