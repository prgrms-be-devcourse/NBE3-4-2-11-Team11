package com.pofo.backend.domain.user.logout.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.common.security.cookie.TokenCookieUtil;
import com.pofo.backend.domain.user.logout.dto.UserLogoutResponseDto;
import com.pofo.backend.domain.user.logout.service.UserLogoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserLogoutController {

    private final UserLogoutService userLogoutService;
    private final TokenCookieUtil tokenCookieUtil;

    @PostMapping("/logout")
    public ResponseEntity<RsData<UserLogoutResponseDto>> logout(
            @CookieValue(name = "accessCookie", required = false) String accessToken,  // ✅ 쿠키에서 accessToken 가져오기
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ) {
        if (accessToken == null || accessToken.isEmpty()) {
           return ResponseEntity.badRequest().body(new RsData<>("400", "accessToken 쿠키 없음", null));
        }

        UserLogoutResponseDto responseDto = userLogoutService.logout(
                accessToken,
                httpServletRequest,
                httpServletResponse);

        //  로그아웃 시 모든 쿠키 삭제
        tokenCookieUtil.cleanTokenCookies(httpServletResponse,"accessCookie");
        tokenCookieUtil.cleanTokenCookies(httpServletResponse,"refreshCookie");

        return ResponseEntity.ok(
                new RsData<>(responseDto.getResultCode(), responseDto.getMessage(), responseDto));
    }
}