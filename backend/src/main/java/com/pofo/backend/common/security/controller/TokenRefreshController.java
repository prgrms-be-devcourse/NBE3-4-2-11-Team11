package com.pofo.backend.common.security.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.common.security.dto.TokenDto;
import com.pofo.backend.common.security.jwt.TokenProvider;
import com.pofo.backend.domain.user.join.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

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

    @Value("${jwt.expiration.time}")
    private Long validationTime;

    @PostMapping("/refresh")
    public ResponseEntity<RsData<TokenDto>> refreshToken(HttpServletRequest request,  HttpServletResponse response) {

        // ✅ Refresh Token & Access Token 각각 쿠키에서 추출
        String refreshToken = extractRefreshTokenFromCookies(request);
        String accessToken = extractAccessTokenFromCookies(request);

        // 🚨 Refresh Token이 없거나 유효하지 않으면 로그아웃 처리
        if (refreshToken == null || refreshToken.isEmpty() || !tokenProvider.validateToken(refreshToken)) {
            log.error("🚨 Refresh Token이 유효하지 않음 → 로그아웃 필요");
            return ResponseEntity.status(401).body(new RsData<>("401", "Refresh Token이 유효하지 않음", new TokenDto()));
        }

        // ✅ Refresh Token이 유효하면 Authentication 가져오기
        Authentication authentication = tokenProvider.getAuthenticationFromRefreshToken(refreshToken);
        if (authentication == null) {
            log.error("🚨 인증 정보를 가져올 수 없음 → 로그아웃 필요");
            return ResponseEntity.status(401).body(new RsData<>("401", "인증 정보를 가져올 수 없음", new TokenDto()));
        }

        // ✅ 현재 Access Token의 남은 만료 시간 확인
        long currentAccessTokenExpiry = 0;
        if (accessToken != null && !accessToken.isEmpty() && tokenProvider.validateToken(accessToken)) {
            currentAccessTokenExpiry = tokenProvider.getExpiration(accessToken);
        }

        long thresholdTime = validationTime / 5; // 만료시간의 20% 이하일 때만 갱신

        // ✅ accessToken이 없으면 Refresh Token으로 갱신 시도
        if (accessToken == null || accessToken.isEmpty()) {
            log.warn("⚠️ Access Token이 존재하지 않음 → Refresh Token으로 재발급 시도");

            // ✅ 새 Access Token 발급
            String newAccessToken = tokenProvider.generateAccessToken(authentication);

            // ✅ 새 Access Token을 쿠키에 저장
            response.addHeader("Set-Cookie", "accessCookie=" + newAccessToken + "; Path=/; HttpOnly");

            log.info("🔄 Access Token이 없어서 Refresh Token으로 새 Access Token 발급 완료!");
            return ResponseEntity.ok(new RsData<>("200", "Access Token이 없어서 새로 발급됨",
                    TokenDto.builder()
                            .accessToken(newAccessToken)
                            .refreshToken(refreshToken)
                            .type("Bearer")
                            .accessTokenValidationTime(null)
                            .refreshTokenValidationTime(null)
                            .build()
            ));
        }

        // ✅ Access Token 만료 시간이 20% 이하로 남았을 때만 갱신
        if (currentAccessTokenExpiry < thresholdTime) {
            // ✅ 새 Access Token 발급
            String newAccessToken = tokenProvider.generateAccessToken(authentication);

            // ✅ 새 Access Token을 쿠키에 저장
            response.addHeader("Set-Cookie", "accessCookie=" + newAccessToken + "; Path=/; HttpOnly");

            TokenDto newTokenResponse = TokenDto.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken) // 기존 Refresh Token 유지
                    .build();

            log.info("✅ Access Token 갱신 완료");
            return ResponseEntity.ok(new RsData<>("200", "Access Token 갱신 성공", newTokenResponse));
        }

        log.info("⏳ Access Token 아직 유효 (남은 시간: " + currentAccessTokenExpiry + "ms)");
        return ResponseEntity.ok(new RsData<>("200", "Access Token이 아직 유효합니다.", new TokenDto()));

    }

    /**
     * HttpOnly 쿠키에서 Refresh Token 가져오는 메서드
     */
    private String extractRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refreshCookie")) {
                return cookie.getValue();
            }
        }

        return null;
    }

    private String extractAccessTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessCookie".equals(cookie.getName())) { // ✅ Access Token 쿠키 이름
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}