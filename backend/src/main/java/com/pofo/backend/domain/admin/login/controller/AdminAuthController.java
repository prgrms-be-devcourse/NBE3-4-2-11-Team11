package com.pofo.backend.domain.admin.login.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.common.security.cookie.TokenCookieUtil;
import com.pofo.backend.common.security.dto.TokenDto;
import com.pofo.backend.common.security.jwt.TokenProvider;
import com.pofo.backend.domain.admin.login.dto.AdminLoginRequest;
import com.pofo.backend.domain.admin.login.dto.AdminLoginResponse;
import com.pofo.backend.domain.admin.login.dto.AdminLogoutResponse;
import com.pofo.backend.domain.admin.login.entitiy.Admin;
import com.pofo.backend.domain.admin.login.service.AdminService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final AdminService adminService;
    private final BCryptPasswordEncoder passwordEncoder;
//    private final TokenBlacklistService tokenBlacklistService;
    private final TokenCookieUtil tokenCookieUtil; // ✅ TokenCookieUtil 추가

    @PostMapping("/login")
    public ResponseEntity<RsData<AdminLoginResponse>> login(
            @RequestBody AdminLoginRequest request, HttpServletResponse response) {
        try {
            // ✅ 인증 시도
            Authentication authentication = authenticationManager.authenticate(request.getAuthenticationToken());
            adminService.recordLoginSuccess(request.getUsername());

            // ✅ JWT 토큰 생성
            TokenDto token = tokenProvider.createToken(authentication);

            // ✅ Access Token & Refresh Token을 HttpOnly 쿠키에 저장
            tokenCookieUtil.setTokenCookies(response, token.getAccessToken(), token.getRefreshToken());

            // ✅ 로그인 성공 응답
            return ResponseEntity.ok(new RsData<>("200", "로그인 성공", new AdminLoginResponse("로그인 성공")));
        } catch (AuthenticationException e) {
            return handleAuthenticationException(request);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<RsData<AdminLogoutResponse>> logout(HttpServletResponse response) {
        // ✅ 로그아웃 시 쿠키 삭제
        tokenCookieUtil.cleanTokenCookies(response, "accessCookie");
        tokenCookieUtil.cleanTokenCookies(response, "refreshCookie");

        return ResponseEntity.ok(new RsData<>("200", "성공적으로 로그아웃되었습니다.", new AdminLogoutResponse("로그아웃 성공")));
    }

    // ✅ 로그인 실패 처리 로직 유지
    private ResponseEntity<RsData<AdminLoginResponse>> handleAuthenticationException(AdminLoginRequest request) {
        Optional<Admin> optionalAdmin = adminService.findByUsername(request.getUsername());
        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
            if (admin.getStatus() == Admin.Status.INACTIVE) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new RsData<>("401", "계정이 비활성화 상태입니다.", new AdminLoginResponse("비활성화됨")));
            } else {
                if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                    adminService.recordLoginFailure(request.getUsername());
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new RsData<>("401",
                                    "비밀번호가 일치하지 않습니다 (틀린 횟수 " + admin.getFailureCount() + "회)",
                                    new AdminLoginResponse("비밀번호 불일치")));
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new RsData<>("401", "아이디 또는 비밀번호가 틀렸습니다.", new AdminLoginResponse("로그인 실패")));
    }
}
