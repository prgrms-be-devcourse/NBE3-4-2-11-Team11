package com.pofo.backend.domain.user.logout.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLogoutResponseDto {
    private final String message;
    private final String resultCode;
}
