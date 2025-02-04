package com.pofo.backend.domain.admin.login.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.common.security.dto.TokenDto;
import com.pofo.backend.common.security.jwt.TokenProvider;
import com.pofo.backend.domain.admin.login.dto.AdminLoginRequest;
import com.pofo.backend.domain.admin.login.dto.AdminLoginResponse;
import com.pofo.backend.domain.admin.login.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<RsData<AdminLoginResponse>> login(@RequestBody AdminLoginRequest request) {
        try {
            // 인증 시도
            Authentication authentication = authenticationManager.authenticate(request.getAuthenticationToken());

            // 로그인 성공 시 실패 이력 초기화
            adminService.recordLoginSuccess(request.getUsername());

            // JWT 토큰 생성
            TokenDto token = tokenProvider.createToken(authentication);

            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + token.getAccessToken())
                    .header("Refresh-Token", token.getRefreshToken())
                    .body(new RsData<>("200", "로그인 성공", new AdminLoginResponse("로그인 성공")));
        } catch (AuthenticationException e) {
            // 인증 실패 시 실패 이력 증가
            adminService.recordLoginFailure(request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new RsData<>("401", "로그인 실패: 아이디 또는 비밀번호가 올바르지 않습니다.",new AdminLoginResponse("로그인 실패")));
        }
    }
}
