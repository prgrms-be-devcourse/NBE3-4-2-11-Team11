package com.pofo.backend.domain.user.login.controller;


import com.pofo.backend.common.exception.SocialLoginException;
import com.pofo.backend.domain.user.login.service.UserLoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
public class UserLoginController {

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String naverRedirectUri;

    private final UserLoginService userLoginService;

    public UserLoginController(UserLoginService userLoginService) {
        this.userLoginService = userLoginService;
    }

    @GetMapping("/naver/login")
    public ResponseEntity<Void> naverLogin(HttpSession session) {
        String state = UUID.randomUUID().toString(); //  랜덤 상태값 설정
        session.setAttribute("naver_state", state); //  세션 저장 ( naver url 콜백 시 검증 )

        String naverLoginUrl = "https://nid.naver.com/oauth2.0/authorize?response_type=code"
                + "&client_id=" + naverClientId
                + "&redirect_uri=" + naverRedirectUri
                +"$state="+ state;

        return ResponseEntity.status(HttpStatus.FOUND) // 302 리디렉트 응답
                .header(HttpHeaders.LOCATION, naverRedirectUri)
                .build();

    }

    @GetMapping("/naver/login/naver/callback")
    public ResponseEntity<Void> naverCallback (
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            HttpSession session) {

        String storedState = (String) session.getAttribute("naver_state");

        if (storedState == null || !storedState.equals(state)) {
            throw new SocialLoginException("잘못된 접근입니다.");
        }

        // 네이버 로그인 처리
        userLoginService.processNaverLogin(code, state);

        //  로그인 성공 후 리디렉트할 페이지 설정
        String redirectUrl = "/";

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION,redirectUrl)
                .build();
    }
}
