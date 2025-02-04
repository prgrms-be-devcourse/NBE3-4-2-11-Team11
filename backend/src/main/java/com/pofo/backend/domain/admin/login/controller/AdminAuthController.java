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
        TokenDto token = tokenProvider.createToken(authentication);

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token.getAccessToken())
                .header("Refresh-Token", token.getRefreshToken())
                .body(new RsData<>("200", "로그인 성공", new AdminLoginResponse()));
    }

}
