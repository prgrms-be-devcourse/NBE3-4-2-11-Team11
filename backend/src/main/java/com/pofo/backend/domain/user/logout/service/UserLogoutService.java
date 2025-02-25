package com.pofo.backend.domain.user.logout.service;

import com.pofo.backend.common.security.jwt.TokenProvider;
import com.pofo.backend.domain.user.logout.dto.UserLogoutResponseDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLogoutService {

    private final TokenProvider tokenProvider;

    public UserLogoutResponseDto logout(
            String token,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        log.info("🚪 로그아웃 요청 받음: Token = {}", token);

        // Access Token 유효성 검사 (추후 토큰 무효화 로직 추가 가능)
        if (tokenProvider.validateToken(token)) {
            long expirationTime = tokenProvider.getTokenExpirationTime(token);
            if (expirationTime > 0) {
                log.info("토큰 만료 시간: {} 초 (토큰 무효화 로직은 추후 구현 예정)", expirationTime / 1000);
            }
        } else {
            log.warn("❌ 유효하지 않은 Access Token으로 로그아웃 요청");
        }

        // 클라이언트 쿠키에서 Refresh Token 삭제
        Cookie refreshCookie = new Cookie("refreshCookie", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);  // 즉시 삭제
        response.addCookie(refreshCookie);

        log.info("🔓 클라이언트 Refresh Token 쿠키 삭제 완료");

        // 클라이언트 세션 무효화
        request.getSession().invalidate();
        log.info("🔓 클라이언트 세션 무효화 완료");

        return UserLogoutResponseDto.builder()
                .message("로그아웃이 완료되었습니다.")
                .resultCode("200")
                .build();
    }
}
