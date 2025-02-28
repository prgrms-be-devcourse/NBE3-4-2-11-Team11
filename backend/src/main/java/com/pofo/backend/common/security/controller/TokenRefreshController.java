package com.pofo.backend.common.security.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.common.security.dto.TokenDto;
import com.pofo.backend.common.security.jwt.TokenProvider;
import com.pofo.backend.domain.user.join.entity.User;
import com.pofo.backend.domain.user.join.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
/*
*
*  AccessToken 만료 시, RefreshToken을 이용하여 AccessToken 재요청 하기 위한 컨트롤러,
*  공통 부품으로 쓰기 위해 common/security/controller에 적재.
*
*/

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/token")
public class TokenRefreshController {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/refresh")
    public ResponseEntity<RsData<TokenDto>> refreshToken(HttpServletRequest request,  HttpServletResponse response) {
        log.info("토큰 재발급 시작");

        String  refreshToken = extractRefreshTokenFromCookies(request);
        log.info("🔍 쿠키에서 Refresh Token 가져옴: {}", refreshToken);

        // Refresh Token 유효성 검사
        if (refreshToken == null || refreshToken.isEmpty() || !tokenProvider.validateToken(refreshToken)) {
            log.warn("❌ Refresh Token이 유효하지 않음.");
            return ResponseEntity.status(401).body(
                    new RsData<>("401", "Refresh Token이 유효하지 않음",
                            TokenDto.builder()
                                    .accessToken("")
                                    .refreshToken("")
                                    .type("Bearer")
                                    .accessTokenValidationTime(0L)
                                    .refreshTokenValidationTime(0L)
                                    .build()
                    )
            );
        }

        // 토큰에서 인증 정보 획득 (관리자/일반 사용자를 구분하여 조회)
        Authentication authentication = tokenProvider.getAuthenticationFromRefreshToken(refreshToken);
        if (authentication == null) {
            log.warn("❌ 인증 정보를 가져올 수 없음.");
            return ResponseEntity.status(401)
                    .body(new RsData<>("401", "인증 정보를 가져올 수 없음", null));
        }

        // 새 Access Token 발급
        String newAccessToken = tokenProvider.generateAccessToken(authentication);
        log.info("✅ 새로운 Access Token 발급 완료: {}", newAccessToken);

        // ✅ Set-Cookie로 새로운 accessCookie 설정
        response.addHeader("Set-Cookie", "accessCookie=" + newAccessToken + "; Path=/; HttpOnly; Secure; SameSite=None");

        TokenDto newTokenResponse = TokenDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // 기존 Refresh Token 유지
                .build();

        return ResponseEntity.ok(new RsData<>("200", "Access Token 갱신 성공", newTokenResponse));
    }

    /**
     * HttpOnly 쿠키에서 Refresh Token 가져오는 메서드
     */
    private String extractRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            log.warn("⚠️ 쿠키가 존재하지 않음.");
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refreshCookie")) {
                log.info("🔍 쿠키에서 Refresh Token 가져옴: {}", cookie.getValue());
                return cookie.getValue();
            }
        }

        log.warn("⚠️ Refresh Token 쿠키를 찾을 수 없음.");
        return null;
    }

}
