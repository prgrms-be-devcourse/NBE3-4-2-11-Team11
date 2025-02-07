package com.pofo.backend.domain.admin.login.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.common.security.dto.TokenDto;
import com.pofo.backend.common.security.jwt.TokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class RefreshTokenController {

    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @PostMapping("/refresh-token")
    public ResponseEntity<RsData<TokenDto>> refreshToken(
            @RequestHeader(value = "Refresh-Token", required = false) String refreshToken) {
        try {
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new RsData<>("400", "Refresh Token이 제공되지 않았습니다.", null));
            }
            if (!tokenProvider.validateToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new RsData<>("401", "Refresh Token이 유효하지 않습니다.", null));
            }

            long remainingMillis = tokenProvider.getExpiration(refreshToken);
            final long THREE_DAYS_IN_MILLIS = 3 * 24 * 60 * 60 * 1000L;

            Authentication authentication = tokenProvider.getAuthenticationFromRefreshToken(refreshToken);
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new RsData<>("401", "인증 정보를 가져올 수 없습니다.", null));
            }

            String newAccessToken = tokenProvider.generateAccessToken(authentication);
            if (newAccessToken == null || newAccessToken.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new RsData<>("500", "새로운 Access Token 생성 중 오류 발생", null));
            }

            String newRefreshToken = refreshToken;
            if (remainingMillis <= THREE_DAYS_IN_MILLIS) {
                long now = System.currentTimeMillis();
                newRefreshToken = Jwts.builder()
                        .setSubject(authentication.getName())
                        .setExpiration(new Date(now + tokenProvider.getRefreshTokenValidationTime()))
                        .signWith(tokenProvider.getKey(), SignatureAlgorithm.HS512)
                        .compact();
                redisTemplate.opsForValue().set(newRefreshToken, "valid",
                        tokenProvider.getRefreshTokenValidationTime(), TimeUnit.MILLISECONDS);
            }

            TokenDto newTokenDto = TokenDto.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .accessTokenValidationTime(tokenProvider.getValidationTime())
                    .refreshTokenValidationTime(tokenProvider.getRefreshTokenValidationTime())
                    .type("Bearer")
                    .build();

            return ResponseEntity.ok(new RsData<>("200", "새로운 토큰이 발급되었습니다.", newTokenDto));

        } catch (Exception e) {
            log.error("Exception occurred in refreshToken: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RsData<>("500", "서버 내부 오류가 발생했습니다.", null));
        }
    }
}
