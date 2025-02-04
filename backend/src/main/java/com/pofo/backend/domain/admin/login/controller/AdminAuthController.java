package com.pofo.backend.domain.admin.login.controller;

import com.pofo.backend.common.base.Empty;
import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.common.security.dto.TokenDto;
import com.pofo.backend.common.security.jwt.TokenProvider;
import com.pofo.backend.domain.admin.login.dto.AdminLoginRequest;
import com.pofo.backend.domain.admin.login.dto.AdminLoginResponse;
import com.pofo.backend.domain.admin.login.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final AdminService adminService;
    private final RedisTemplate<String, String> redisTemplate;  // Redis 템플릿 주입

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
                    .body(new RsData<>("401", "로그인 실패: 아이디 또는 비밀번호가 올바르지 않습니다.",
                            new AdminLoginResponse("로그인 실패")));
        }
    }

    // 로그아웃 엔드포인트 추가 (JWT 블랙리스트 방식)
    @PostMapping("/logout")
    public ResponseEntity<RsData<Empty>> logout(HttpServletRequest request) {
        // 헤더에서 "Authorization" 토큰 추출 (Bearer 토큰 형식)
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            // 토큰 유효성 검사
            if (tokenProvider.validateToken(token)) {
                // 토큰의 남은 유효시간 계산 (예: 만료까지 남은 밀리초)
                long remainingMillis = tokenProvider.getExpiration(token) - System.currentTimeMillis();
                // Redis에 토큰을 블랙리스트로 등록 (만료 시간과 함께)
                redisTemplate.opsForValue().set(token, "logout", remainingMillis, TimeUnit.MILLISECONDS);
            }
        }
        return ResponseEntity.ok(new RsData<>("200", "로그아웃 성공"));
    }
}
