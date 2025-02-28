package com.pofo.backend.domain.user.login.controller;


import com.pofo.backend.common.exception.SocialLoginException;
import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.common.security.cookie.TokenCookieUtil;
import com.pofo.backend.domain.user.join.entity.Oauth;
import com.pofo.backend.domain.user.login.dto.UserLoginResponseDto;
import com.pofo.backend.domain.user.login.service.UserLoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/v1/user")
public class UserLoginController {

    //  Naver Oauths 정보 시작 
    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String naverRedirectUri;
    //  Naver Oauths 정보 끝 

    //  Kakao Oauths 정보 시작 
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;
    //  Kakao Oauths 정보 끝

    //  Google Oauths 정보 시작
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;
    //  Google Oauths 정보 끝
    
    private final UserLoginService userLoginService;

    private final TokenCookieUtil tokenCookieUtil; //

    public UserLoginController(UserLoginService userLoginService, TokenCookieUtil tokenCookieUtil) {

        this.userLoginService = userLoginService;
        this.tokenCookieUtil = tokenCookieUtil;
    }

    @GetMapping("/naver/login")
    public ResponseEntity<Void> naverLogin(HttpSession session) {
        String state = UUID.randomUUID().toString(); //  랜덤 상태값 설정
        session.setAttribute("naver_state", state); //  세션 저장 ( naver url 콜백 시 검증 )

        String naverLoginUrl = "https://nid.naver.com/oauth2.0/authorize?response_type=code"
                + "&client_id=" + naverClientId
                + "&redirect_uri=" + naverRedirectUri
                + "&state="+ state;

        return ResponseEntity.status(HttpStatus.FOUND) // 302 리디렉트 응답
                .header(HttpHeaders.LOCATION, naverLoginUrl)
                .build();

    }

    @GetMapping("/naver/login/naver/callback")
    public ResponseEntity<Void> naverCallback (
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            HttpSession session) {

        String storedState = (String) session.getAttribute("naver_state");

        if (storedState == null || !storedState.equals(state)) {
            session.removeAttribute("naver_state"); // 불일치 시 세션 값 제거
            throw new SocialLoginException("잘못된 접근입니다.");
        }

        //  f/e의 callback 페이지로 리디렉트
        String redirectUrl = "http://localhost:3000/login/callback?provider=NAVER&code=" + code + "&state=" + state;


        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION,redirectUrl)
                .build();
    }

    @GetMapping("/naver/login/process")
    public ResponseEntity<?> processNaverLogin(
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            HttpServletResponse response
    ) {

        try{
            //  네이버 로그인 처리
            UserLoginResponseDto responseDto = userLoginService.processNaverLogin(Oauth.Provider.NAVER ,code, state);

            //  토큰을 쿠키에 저장
            tokenCookieUtil.setTokenCookies(response, responseDto.getToken(), responseDto.getRefreshToken());

            //  마지막 로그인한 소셜 플랫폼 제공자를 쿠키에 저장
            saveLastLoginProvider(response,"NAVER");

            return ResponseEntity.ok(new RsData<>(responseDto.getResultCode(),responseDto.getMessage(),responseDto));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @GetMapping("/kakao/login")
    public ResponseEntity<Void> kakaoLogin(HttpSession session) {
        String state = UUID.randomUUID().toString(); //  랜덤 상태값 설정
        session.setAttribute("kakao_state", state); //  세션 저장 ( naver url 콜백 시 검증 )

        String kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize?response_type=code"
                + "&client_id=" + kakaoClientId
                + "&redirect_uri=" + kakaoRedirectUri
                + "&state="+ state;

        return ResponseEntity.status(HttpStatus.FOUND) // 302 리디렉트 응답
                .header(HttpHeaders.LOCATION, kakaoLoginUrl)
                .build();
    }

    @GetMapping("/kakao/login/kakao/callback")
    public ResponseEntity<Void> kakaoCallback (
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            HttpSession session) {

        String storedState = (String) session.getAttribute("kakao_state");

        if (storedState == null || !storedState.equals(state)) {
            session.removeAttribute("kakao_state"); // 불일치 시 세션 값 제거
            throw new SocialLoginException("잘못된 접근입니다.");
        }

        //  f/e의 KakaoCallback 페이지로 리디렉트
        String redirectUrl = "http://localhost:3000/login/callback?provider=KAKAO&code=" + code + "&state=" + state;

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION,redirectUrl)
                .build();
    }

    @GetMapping("/kakao/login/process")
    public ResponseEntity<?> processKakaoLogin(
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            HttpServletResponse response
    ) {

        try{
            //  카카오 로그인 처리
            UserLoginResponseDto responseDto = userLoginService.processKakaoLogin(Oauth.Provider.KAKAO, code, state);

            //  토큰을 쿠키에 저장
            tokenCookieUtil.setTokenCookies(response, responseDto.getToken(), responseDto.getRefreshToken());

            //  마지막 로그인한 소셜 플랫폼 제공자를 쿠키에 저장
            saveLastLoginProvider(response,"KAKAO");

            return ResponseEntity.ok(new RsData<>(responseDto.getResultCode(),responseDto.getMessage(),responseDto));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/google/login")
    public ResponseEntity<Void> googleLogin(HttpSession session) {
        String state = UUID.randomUUID().toString(); //  랜덤 상태값 설정
        session.setAttribute("google_state", state); //  세션 저장 ( google url 콜백 시 검증 )

        String googleLoginUrl = "https://accounts.google.com/o/oauth2/auth?"
                + "response_type=code"
                + "&client_id=" + googleClientId
                + "&redirect_uri=" + googleRedirectUri
                + "&scope=email%20profile"
                + "&access_type=offline"; // refresh token 요청

        return ResponseEntity.status(HttpStatus.FOUND) // 302 리디렉트 응답
                .header(HttpHeaders.LOCATION, googleLoginUrl)
                .build();

    }

    @GetMapping("/google/login/google/callback")
    public ResponseEntity<Void> googleCallback (
            @RequestParam("code") String code,
            HttpSession session) {

        String storedState = (String) session.getAttribute("google_state");

        if (storedState == null) {
            session.removeAttribute("google_state"); // 불일치 시 세션 값 제거
            throw new SocialLoginException("잘못된 접근입니다.");
        }

        //  f/e의 callback 페이지로 리디렉트
        String redirectUrl = "http://localhost:3000/login/callback?provider=GOOGLE&code=" + code;


        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION,redirectUrl)
                .build();
    }

    @GetMapping("/google/login/process")
    public ResponseEntity<?> processGoogleLogin(
            @RequestParam("code") String code,
            HttpServletResponse response
    ) {

        try{
            //  구글 로그인 처리
            UserLoginResponseDto responseDto = userLoginService.processGoogleLogin(Oauth.Provider.GOOGLE ,code);

            //  토큰을 쿠키에 저장
            tokenCookieUtil.setTokenCookies(response, responseDto.getToken(), responseDto.getRefreshToken());

            //  마지막 로그인한 소셜 플랫폼 제공자를 쿠키에 저장
            saveLastLoginProvider(response,"GOOGLE");

            return ResponseEntity.ok(new RsData<>(responseDto.getResultCode(),responseDto.getMessage(),responseDto));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/last-login-provider")
    public ResponseEntity<Map<String, String>> getLastLoginProvider(
            @CookieValue(value = "lastLoginProvider", required = false) String provider
    ) {
        if (provider == null) {
            return ResponseEntity.ok(Collections.singletonMap("lastLoginProvider", "NONE")); // 쿠키가 없으면 "NONE" 반환
        }

        return ResponseEntity.ok(Collections.singletonMap("lastLoginProvider", provider));
    }

    private void saveLastLoginProvider(HttpServletResponse response, String provider) {
        Cookie providerCookie = new Cookie("lastLoginProvider", provider);
        providerCookie.setHttpOnly(true);  // JS에서 접근 불가
        providerCookie.setSecure(true);  // HTTPS에서만 전송
        providerCookie.setPath("/");  // 전체 도메인에서 접근 가능
        providerCookie.setMaxAge(60 * 60 * 24 * 30);  // 30일 유지
        response.addCookie(providerCookie);
    }
}