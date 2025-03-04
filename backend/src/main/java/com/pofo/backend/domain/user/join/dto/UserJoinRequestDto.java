package com.pofo.backend.domain.user.join.dto;

import com.pofo.backend.domain.user.join.entity.Oauth;
import com.pofo.backend.domain.user.join.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserJoinRequestDto {

    @NotNull(message = "provider 값이 없습니다. ")
    private Oauth.Provider provider;

    @NotNull(message = "identify 값이 필요합니다.")
    private String identify;

    @NotNull(message = "email 값이 필요합니다.")
    private String email;

    @NotBlank(message = "이름을 입력 해 주세요.")
    private String name;

    @NotBlank(message = "닉네임을 입력 해 주세요.")
    private String nickname;

    @NotNull(message = "성별을 선택 해 주세요.")
    //private Sex sex;
    private User.Sex sex;

    private LocalDate age;

    private String jobInterest;

    private User.UserStatus userStatus;
}
