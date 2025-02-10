package com.pofo.backend.domain.admin.login.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.common.security.dto.TokenDto;
import com.pofo.backend.common.security.jwt.TokenProvider;
import com.pofo.backend.common.service.TokenBlacklistService;
import com.pofo.backend.domain.admin.login.dto.AdminLoginRequest;
import com.pofo.backend.domain.admin.login.dto.AdminLoginResponse;
import com.pofo.backend.domain.admin.login.dto.AdminLogoutResponse;
import com.pofo.backend.domain.admin.login.entitiy.Admin;
import com.pofo.backend.domain.admin.login.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final AdminService adminService;
    private final RedisTemplate<String, String> redisTemplate;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenBlacklistService tokenBlacklistService; // 분리된 블랙리스트 서비스 주입

    @PostMapping("/login")
    public ResponseEntity<RsData<AdminLoginResponse>> login(@RequestBody AdminLoginRequest request) {
        try {
            // 인증 시도
            Authentication authentication = authenticationManager.authenticate(request.getAuthenticationToken());
            adminService.recordLoginSuccess(request.getUsername());

            // JWT 토큰 생성
            TokenDto token = tokenProvider.createToken(authentication);

            // Refresh Token을 Redis에 저장 (유효시간 설정)
            redisTemplate.opsForValue().set(token.getRefreshToken(), "valid",
                    tokenProvider.getRefreshTokenValidationTime(), TimeUnit.MILLISECONDS);

            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + token.getAccessToken())
                    .header("Refresh-Token", token.getRefreshToken())
                    .body(new RsData<>("200", "로그인 성공", new AdminLoginResponse("로그인 성공")));
        } catch (AuthenticationException e) {
            Optional<Admin> optionalAdmin = adminService.findByUsername(request.getUsername());
            if (optionalAdmin.isPresent()) {
                Admin admin = optionalAdmin.get();
                if (admin.getStatus() == Admin.Status.INACTIVE) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new RsData<>("401", "계정이 비활성화 상태입니다.",
                                    new AdminLoginResponse("계정이 비활성화 상태입니다.")));
                } else {
                    if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                        adminService.recordLoginFailure(request.getUsername());
                        admin = adminService.findByUsername(request.getUsername()).orElse(admin);
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(new RsData<>("401",
                                        "아이디가 맞은 경우 비밀번호가 일치하지 않습니다 (틀린회수 " + admin.getFailureCount() + "회)",
                                        new AdminLoginResponse("비밀번호 불일치")));
                    }
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new RsData<>("401", "아이디 및 비밀번호가 일치하지 않습니다.",
                            new AdminLoginResponse("로그인 실패")));
        }
    }

//    @PostMapping("/logout")
//    public ResponseEntity<RsData<AdminLogoutResponse>> logout(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        // "Bearer " 접두어 제거
//        String token = (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer "))
//                ? bearerToken.substring(7)
//                : null;
//
//        if (token != null && tokenProvider.validateToken(token)) {
//            long remainingMillis = tokenProvider.getExpiration(token);
//            if (remainingMillis > 0) {
//                // 토큰의 남은 유효시간을 기준으로 블랙리스트에 추가 (내부적으로 "blacklist:" 접두어 사용)
//                tokenBlacklistService.addToBlacklist(token, remainingMillis);
//            }
//        }
//        return ResponseEntity.ok(
//                new RsData<>("200", "성공적으로 로그아웃되었습니다.", new AdminLogoutResponse("성공적으로 로그아웃되었습니다."))
//        );
//    }

    @PostMapping("/logout")
    public ResponseEntity<RsData<AdminLogoutResponse>> logout(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // ✅ "Bearer " 접두어 제거
        String token = (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer "))
                ? bearerToken.substring(7)
                : null;

        if (token != null && tokenProvider.validateToken(token)) {
            long remainingMillis = tokenProvider.getExpiration(token);

            // ✅ Access Token을 블랙리스트에 추가 (만료되지 않았을 때만)
            if (remainingMillis > 0) {
                log.info("🛑 Admin 로그아웃: Access Token 블랙리스트 추가 (TTL: {} 초)", remainingMillis / 1000);
                tokenBlacklistService.addToBlacklist(token, remainingMillis);
            }

            // ✅ Redis에서 Refresh Token 삭제
            String refreshTokenKey = "refresh_token:" + token;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(refreshTokenKey))) {
                redisTemplate.delete(refreshTokenKey);
                log.info("✅ Redis에서 Admin의 Refresh Token 삭제 완료");
            } else {
                log.warn("⚠️ Admin의 Refresh Token이 Redis에 존재하지 않음");
            }
        } else {
            log.warn("❌ 유효하지 않은 Access Token으로 로그아웃 요청");
        }

        return ResponseEntity.ok(
                new RsData<>("200", "성공적으로 로그아웃되었습니다.", new AdminLogoutResponse("성공적으로 로그아웃되었습니다."))
        );
    }

}
