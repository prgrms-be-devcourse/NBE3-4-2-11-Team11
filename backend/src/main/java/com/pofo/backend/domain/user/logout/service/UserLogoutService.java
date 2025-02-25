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

        // ✅ 클라이언트 쿠키에서 Refresh Token 삭제
        Cookie refreshCookie = new Cookie("refreshCookie", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);  // 즉시 삭제
        response.addCookie(refreshCookie);

        log.info("🔓 클라이언트 Refresh Token 쿠키 삭제 완료");

        // ✅ 클라이언트 세션 무효화
        request.getSession().invalidate();
        log.info("🔓 클라이언트 세션 무효화 완료");

        return UserLogoutResponseDto.builder()
                .message("로그아웃이 완료되었습니다.")
                .resultCode("200")
                .build();
    }
}