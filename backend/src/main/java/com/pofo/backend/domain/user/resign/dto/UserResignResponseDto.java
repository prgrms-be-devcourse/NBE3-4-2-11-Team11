package com.pofo.backend.domain.user.resign.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResignResponseDto {
    private final String message;
    private final String resultCode;
}
