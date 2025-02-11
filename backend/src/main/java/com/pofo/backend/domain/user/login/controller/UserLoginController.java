package com.pofo.backend.domain.user.login.controller;


import com.pofo.backend.common.exception.SocialLoginException;
import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.user.join.entity.Oauth;
import com.pofo.backend.domain.user.login.dto.UserLoginResponseDto;
import com.pofo.backend.domain.user.login.service.UserLoginService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
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
            log.error(" 네이버 OAuth 실패 - 세션 state 불일치 | 요청 state: {} | 저장된 state: {}", state, storedState);
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
            @RequestParam("state") String state
    ) {

        try{
            //  네이버 로그인 처리
            UserLoginResponseDto responseDto = userLoginService.processNaverLogin(Oauth.Provider.NAVER ,code, state);

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
            log.error(" 카카오 OAuth 실패 - 세션 state 불일치 | 요청 state: {} | 저장된 state: {}", state, storedState);
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
            @RequestParam("state") String state
    ) {

        try{
            //  카카오 로그인 처리
            UserLoginResponseDto responseDto = userLoginService.processKakaoLogin(Oauth.Provider.KAKAO, code, state);

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
            @RequestParam("state") String state
    ) {

        try{
            //  구글 로그인 처리
            UserLoginResponseDto responseDto = userLoginService.processGoogleLogin(Oauth.Provider.GOOGLE ,code);

            return ResponseEntity.ok(new RsData<>(responseDto.getResultCode(),responseDto.getMessage(),responseDto));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}