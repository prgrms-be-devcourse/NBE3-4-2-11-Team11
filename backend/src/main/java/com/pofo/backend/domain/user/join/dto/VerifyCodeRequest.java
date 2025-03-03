package com.pofo.backend.domain.user.join.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VerifyCodeRequest {

    @Email(message = "올바른 이메일 형식이어야 합니다.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    @NotBlank(message = "인증 코드는 필수 입력값입니다.")
    private String code;

    @NotNull(message = "OAuth 제공자를 입력해야 합니다.")
    private String provider;

    @NotBlank(message = "OAuth 식별자는 필수 입력값입니다.")
    private String identify;
}
