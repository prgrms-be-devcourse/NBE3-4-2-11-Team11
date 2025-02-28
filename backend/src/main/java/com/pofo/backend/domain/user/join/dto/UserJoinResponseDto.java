package com.pofo.backend.domain.user.join.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserJoinResponseDto {
    private final String message;
    private final String email;
    private final String resultCode;
}
