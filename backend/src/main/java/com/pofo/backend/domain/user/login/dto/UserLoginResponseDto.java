package com.pofo.backend.domain.user.login.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLoginResponseDto {
    private final String message;
    private final String resultCode;
    private final String token;
    private final String refreshToken;
    private final String provide;
    private final String identify;
    private final String username;
    private final String email;
}