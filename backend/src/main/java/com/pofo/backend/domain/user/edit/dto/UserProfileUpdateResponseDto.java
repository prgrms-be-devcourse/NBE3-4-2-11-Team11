package com.pofo.backend.domain.user.edit.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileUpdateResponseDto {
    private final String message;
    private final String resultCode;
}
