package com.pofo.backend.domain.admin.login.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor  // 모든 필드를 포함하는 생성자 자동 생성
@NoArgsConstructor   // 기본 생성자 자동 생성 (필수는 아님)
public class AdminLoginResponse {
    private String message;
}
