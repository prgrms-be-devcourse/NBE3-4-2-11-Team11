package com.pofo.backend.common.security.controller;

import com.pofo.backend.common.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthStatusController {

    private final TokenProvider tokenProvider;

    @GetMapping("/status")
    public ResponseEntity<?> checkAuthStatus(@CookieValue(value = "accessCookie", required = false) String accessToken) {

        if (accessToken == null) {
            //log.info("🚫 Access Token 없음 → 로그인 상태: false");
            return ResponseEntity.ok(Collections.singletonMap("isLoggedIn", false));
        }

        // ✅ Token 유효성 검증
        boolean isValid = tokenProvider.validateToken(accessToken);

        if (!isValid) {
            //log.warn("⚠️ Access Token이 유효하지 않음 → 로그인 상태: false");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("isLoggedIn", false));
        }

        //log.info("✅ 유효한 Access Token 확인 → 로그인 상태: true");
        return ResponseEntity.ok(Collections.singletonMap("isLoggedIn", true));
    }
}
