package com.pofo.backend.common.security.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.common.security.dto.TokenDto;
import com.pofo.backend.common.security.jwt.TokenProvider;
import com.pofo.backend.domain.user.join.entity.User;
import com.pofo.backend.domain.user.join.repository.UserRepository;
import io.jsonwebtoken.Claims;
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
    public ResponseEntity<RsData<TokenDto>> refreshToken(@RequestBody TokenDto request) {
        log.info("토큰 재발급 시~작");
        String refreshToken = request.getRefreshToken();

        log.info("Refresh token 요청 : {}", refreshToken);

        // ✅ Refresh Token 유효성 검사
        if (refreshToken == null || refreshToken.isEmpty() || !tokenProvider.validateToken(refreshToken)) {
            log.warn("❌ Refresh Token이 유효하지 않음.");
            return ResponseEntity.status(401).body(
                    new RsData<>("401", "Refresh Token이 유효하지 않음",
                            TokenDto.builder()
                                    .accessToken("")  // 빈 Access Token
                                    .refreshToken("")  // 빈 Refresh Token
                                    .type("Bearer")  // 기본 타입 유지
                                    .accessTokenValidationTime(0L)  // 0L 설정
                                    .refreshTokenValidationTime(0L)  // 0L 설정
                                    .build()
                    )
            ); // ✅ null 방지
        }

        // ✅ Refresh Token에서 사용자 정보 가져오기
        Claims claims = tokenProvider.parseData(refreshToken);
        String email = claims.getSubject();

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            log.warn("❌ 해당 이메일({})의 사용자를 찾을 수 없음.", email);
            return ResponseEntity.status(404).body(new RsData<>("404", "사용자를 찾을 수 없음", null));
        }

        User user = userOptional.get();
        Authentication authentication = tokenProvider.getAuthenticationFromRefreshToken(refreshToken);

        // ✅ 새 Access Token 발급
        String newAccessToken = tokenProvider.generateAccessToken(authentication);
        log.info("✅ 새로운 Access Token 발급 완료: {}", newAccessToken);

        // ✅ 새로운 토큰 정보 반환
        TokenDto newTokenResponse = TokenDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // 기존 Refresh Token 유지
                .build();

        return ResponseEntity.ok(new RsData<>("200", "Access Token 갱신 성공", newTokenResponse));
    }

}
