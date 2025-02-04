/*
package com.pofo.backend.common.security;

import com.pofo.backend.common.security.jwt.JwtSecurityConfig;
import com.pofo.backend.common.security.jwt.OAuth2AuthenticationSuccessHandler;
import com.pofo.backend.common.security.jwt.TokenProvider;
import com.pofo.backend.domain.user.login.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    // JWT 토큰을 관리하는 Provider
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtSecurityConfig jwtSecurityConfig; // JwtSecurityConfig를 주입
    private final CustomOAuth2UserService customOAuth2UserService; // CustomOAuth2UserService 주입 
    private final OAuth2AuthenticationSuccessHandler successHandler; // OAuth2AuthenticationSuccessHandler 주입


    // Spring Security의 사용자 인증을 위한 서비스 (AdminDetailsService 추가)
    private final AdminDetailsService adminDetailsService;

    */
/**
     * 비밀번호 암호화를 위한 BCryptPasswordEncoder 등록
     * - 사용자 로그인 시 입력된 비밀번호를 암호화된 비밀번호와 비교할 때 사용
     *//*

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    */
/**
     * AuthenticationManager 설정
     * - Spring Security의 인증을 처리하는 핵심 매니저
     * - AuthenticationConfiguration을 통해 기본 인증 매니저를 가져옴
     *//*

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        // JwtSecurityConfig에서 JwtFilter를 추가하는 설정을 적용
        jwtSecurityConfig.configure(http); // 필터 설정 적용

        return http
                .formLogin().disable()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .requestMatchers(
                        "/api/v1/admin/login",
                        "/api/v1/user/login",
                        "/api/v1/user/naver/login").permitAll() // 로그인 API에 대한 예외 처리
                .anyRequest().authenticated()
                .and()
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(auth -> auth
                                .baseUri("/api/v1/user/oauth2/authorize")) // OAuth2 로그인 엔드포인트
                        .redirectionEndpoint(redir -> redir
                                .baseUri("/api/v1/user/oauth2/callback/*")) // 리다이렉트 URL
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)) // 사용자 정보 가져오기
                        .successHandler(successHandler) // 로그인 성공 후 JWT 발급
                )
                .build();
    }

    */
/**
     * DaoAuthenticationProvider 설정
     * - UserDetailsService를 활용하여 사용자 정보를 로드
     * - BCryptPasswordEncoder를 사용하여 비밀번호 검증
     *//*

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(adminDetailsService); // AdminDetailsService에서 사용자 정보 로드
        provider.setPasswordEncoder(passwordEncoder()); // 비밀번호 암호화 설정
        return provider;
    }

    */
/**
     * Security Filter Chain 설정
     * - JWT 인증 방식을 유지하면서 Spring Security 설정 적용
     *//*

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        jwtSecurityConfig.configure(http);

        return http
                .formLogin().disable() // 기본 로그인 폼 비활성화 (JWT 기반 로그인 사용)
                .csrf().disable() // CSRF 보안 비활성화 (JWT는 CSRF 공격에 안전)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션을 사용하지 않는 STATELESS 정책 적용
                .and()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/admin/login", "/api/v1/user/login").permitAll() // 로그인 API는 인증 없이 접근 가능
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                )
                .authenticationProvider(authenticationProvider()) // DaoAuthenticationProvider 등록
                .build(); // 설정 완료 후 SecurityFilterChain 반환
    }
}*/
package com.pofo.backend.common.security;

import com.pofo.backend.common.security.jwt.JwtSecurityConfig;
import com.pofo.backend.common.security.jwt.OAuth2AuthenticationSuccessHandler;
import com.pofo.backend.common.security.jwt.TokenProvider;
import com.pofo.backend.domain.user.login.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    // JWT 토큰을 관리하는 Provider
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtSecurityConfig jwtSecurityConfig; // JwtSecurityConfig 주입
    private final CustomOAuth2UserService customOAuth2UserService; // CustomOAuth2UserService 주입
    private final OAuth2AuthenticationSuccessHandler successHandler; // OAuth2AuthenticationSuccessHandler 주입

    // Spring Security의 사용자 인증을 위한 서비스 (AdminDetailsService)
    private final AdminDetailsService adminDetailsService;

    /**
     * 비밀번호 암호화를 위한 BCryptPasswordEncoder 등록
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * DaoAuthenticationProvider 설정
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(adminDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * AuthenticationManager 빈 등록
     * - AdminAuthController 등에서 AuthenticationManager를 주입받기 위해 필요
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 통합 SecurityFilterChain 설정
     * - JWT 인증 필터 설정 (jwtSecurityConfig.configure(http))
     * - 폼 로그인 및 CSRF 비활성화, STATELESS 세션 정책 적용
     * - 로그인 API 및 OAuth2 관련 엔드포인트 예외 처리
     * - OAuth2 로그인 성공 시 JWT 발급을 위한 successHandler 적용
     * - DaoAuthenticationProvider 적용
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // JwtSecurityConfig에서 JwtFilter를 추가하는 설정을 적용
        jwtSecurityConfig.configure(http);

        http
                .formLogin().disable() // 기본 폼 로그인 비활성화
                .csrf().disable()      // CSRF 보안 비활성화 (JWT 사용 시 안전)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션을 사용하지 않는 정책
                .and()
                .authorizeHttpRequests(authorize -> authorize
                        // 로그인 API 및 OAuth2 관련 엔드포인트는 예외 처리
                        .requestMatchers("/api/v1/admin/login", "/api/v1/user/login", "/api/v1/user/naver/login").permitAll()
                        .anyRequest().authenticated() // 그 외의 요청은 인증 필요
                )
                .authenticationProvider(authenticationProvider()) // DaoAuthenticationProvider 등록
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(auth -> auth
                                .baseUri("/api/v1/user/oauth2/authorize")) // OAuth2 인증 엔드포인트
                        .redirectionEndpoint(redir -> redir
                                .baseUri("/api/v1/user/oauth2/callback/*")) // OAuth2 리다이렉션 URL
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)) // 사용자 정보 가져오기 서비스
                        .successHandler(successHandler)); // OAuth2 로그인 성공 후 JWT 발급 처리

        return http.build();
    }
}
