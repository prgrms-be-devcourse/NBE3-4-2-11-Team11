package com.pofo.backend.domain.user.join.dto;

import lombok.Getter;

@Getter
public class VerifyCodeRequest {
    private String email;
    private String code;
    private String provider;
    private String identify;
}
