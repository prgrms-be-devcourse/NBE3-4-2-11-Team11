package com.pofo.backend.domain.user.join.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserJoinRequestDto {
    private Provider provider;
    private String identify;
    private String email;

    @NotBlank(message = "이름을 입력 해 주세요.")
    private String name;

    @NotBlank(message = "닉네임을 입력 해 주세요.")
    private String nickname;

    @NotBlank(message = "성별을 선택 해 주세요.")
    private Sex sex;

    private LocalDate age;

    public enum Provider {
        GOOGLE,
        NAVER,
        KAKAO
    }

    public enum Sex {
        FEMALE,
        MALE
    }
}
