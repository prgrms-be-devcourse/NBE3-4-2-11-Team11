package com.pofo.backend.domain.user.login.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLoginResquestDto {
    private String authorizationCode;
    private String provider;
}
