package com.pofo.backend.domain.admin.login.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminLoginResponse {
    private final String message;
    private final String resultCode;
}
