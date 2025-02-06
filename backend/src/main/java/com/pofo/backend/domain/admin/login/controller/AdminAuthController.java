package com.pofo.backend.domain.admin.login.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.common.security.dto.TokenDto;
import com.pofo.backend.common.security.jwt.TokenProvider;
import com.pofo.backend.domain.admin.login.dto.AdminLoginRequest;
import com.pofo.backend.domain.admin.login.dto.AdminLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<RsData<AdminLoginResponse>> login(@RequestBody AdminLoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(request.getAuthenticationToken());

        // 로그인 성공 시 JWT 토큰 생성
        TokenDto token = tokenProvider.createToken(authentication);

        return ResponseEntity.ok(new RsData<>(
                "200", "로그인 성공",
                new AdminLoginResponse(token.getAccessToken(), token.getRefreshToken())
        ));
    }
}
