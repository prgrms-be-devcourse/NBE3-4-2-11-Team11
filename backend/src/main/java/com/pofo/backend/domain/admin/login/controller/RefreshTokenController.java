
package com.pofo.backend.domain.admin.login.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.common.security.dto.TokenDto;
import com.pofo.backend.common.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            // Refresh Token이 전달되지 않았을 경우
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new RsData<>("400", "Refresh Token이 제공되지 않았습니다.", null));
            }

            // Refresh Token 유효성 검사
            if (!tokenProvider.validateToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new RsData<>("401", "Refresh Token이 유효하지 않습니다.", null));
            }

            // TokenProvider의 refreshAccessToken 메소드 호출로 새로운 토큰 쌍 발급
            TokenDto newTokenDto = tokenProvider.refreshAccessToken(refreshToken);

            // (선택사항) 새로운 Refresh Token을 Redis에 저장 (유효시간 설정)
            redisTemplate.opsForValue().set(newTokenDto.getRefreshToken(), "valid",
                    newTokenDto.getRefreshTokenValidationTime(), TimeUnit.MILLISECONDS);

            return ResponseEntity.ok(new RsData<>("200", "새로운 토큰이 발급되었습니다.", newTokenDto));
        } catch (Exception e) {
            log.error("Exception occurred in refreshToken: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RsData<>("500", "서버 내부 오류가 발생했습니다.", null));
        }
    }
}
