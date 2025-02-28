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
            return ResponseEntity.ok(Collections.singletonMap("isLoggedIn", false));
        }

        // ✅ Token 유효성 검증
        boolean isValid = tokenProvider.validateToken(accessToken);

        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("isLoggedIn", false));
        }

        return ResponseEntity.ok(Collections.singletonMap("isLoggedIn", true));
    }
}
