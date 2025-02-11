package com.pofo.backend.domain.user.join.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.common.security.dto.TokenDto;
import com.pofo.backend.common.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * 유저 전용 Refresh Token 컨트롤러
 * - /api/v1/user/refresh-token 엔드포인트에서 새 Access/Refresh 토큰 발급
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserRefreshTokenController {

    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Refresh Token을 받아 새로운 토큰을 발급하는 엔드포인트
     *
     * @param refreshToken 요청 헤더(Refresh-Token)에 담긴 Refresh Token
     * @return 새로운 Access/Refresh Token이 담긴 TokenDto
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<RsData<TokenDto>> refreshToken(
            @RequestHeader(value = "Refresh-Token", required = false) String refreshToken) {
        try {
            // 1) Refresh Token이 제공되지 않았을 경우
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new RsData<>("400", "Refresh Token이 제공되지 않았습니다.", null));
            }

            // 2) Refresh Token 유효성 검사
            if (!tokenProvider.validateToken(refreshToken)) {
                log.warn("❌ 유효하지 않은 Refresh Token: {}", refreshToken);
                redisTemplate.delete(refreshToken); // ✅ 만료된 Refresh Token 삭제
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new RsData<>("401", "Refresh Token이 유효하지 않습니다. 다시 로그인해주세요.", null));
            }

            // 3) 유효하다면 새로운 Access/Refresh Token 발급
            TokenDto newTokenDto = tokenProvider.refreshAccessToken(refreshToken);

            // 4) 기존의 오래된 Refresh Token 삭제 (새로운 토큰으로 업데이트)
            redisTemplate.delete(refreshToken);
            log.info("🗑️ 기존 Refresh Token 삭제: {}", refreshToken);

            // 5) 새로운 Refresh Token을 Redis에 저장 + 만료시간 설정 (초 단위 변환)
            long expireTimeSeconds = newTokenDto.getRefreshTokenValidationTime() / 1000; // ✅ 초 단위 변환
            redisTemplate.opsForValue().set(
                    newTokenDto.getRefreshToken(),
                    "valid",
                    expireTimeSeconds,
                    TimeUnit.SECONDS
            );
            log.info("✅ 새로운 Refresh Token 저장 (TTL: {}초): {}", expireTimeSeconds, newTokenDto.getRefreshToken());

            // 6) 성공적으로 발급된 토큰을 응답
            return ResponseEntity.ok(new RsData<>("200", "새로운 토큰이 발급되었습니다.", newTokenDto));

        } catch (Exception e) {
            log.error("🚨 Exception 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RsData<>("500", "서버 내부 오류가 발생했습니다.", null));
        }
    }
}
