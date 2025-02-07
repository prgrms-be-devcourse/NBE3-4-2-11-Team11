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
            // 1) Refresh Token이 전달되지 않았을 경우
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new RsData<>("400", "Refresh Token이 제공되지 않았습니다.", null));
            }

            // 2) Refresh Token 유효성 검사
            if (!tokenProvider.validateToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new RsData<>("401", "Refresh Token이 유효하지 않습니다.", null));
            }

            // 3) 유효하다면, TokenProvider의 refreshAccessToken()을 호출하여 새로운 토큰 쌍 발급
            TokenDto newTokenDto = tokenProvider.refreshAccessToken(refreshToken);

            // (선택사항) 새로운 Refresh Token을 Redis에 저장 + 유효시간 설정
            // 보통 "refreshTokenValue" -> "valid" 식으로 키/값을 넣으며, 만료시간은 refreshTokenValidationTime 밀리초로
            redisTemplate.opsForValue().set(
                    newTokenDto.getRefreshToken(),
                    "valid",
                    newTokenDto.getRefreshTokenValidationTime(),
                    TimeUnit.MILLISECONDS
            );

            // 4) 성공적으로 발급된 토큰을 RsData 포맷으로 응답
            return ResponseEntity.ok(new RsData<>("200", "새로운 토큰이 발급되었습니다.", newTokenDto));

        } catch (Exception e) {
            log.error("Exception occurred in refreshToken: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RsData<>("500", "서버 내부 오류가 발생했습니다.", null));
        }
    }
}
