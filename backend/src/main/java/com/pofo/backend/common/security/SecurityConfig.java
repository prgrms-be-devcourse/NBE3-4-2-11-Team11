package com.pofo.backend.common.security;

import com.pofo.backend.common.security.jwt.JwtSecurityConfig;
import com.pofo.backend.common.security.jwt.TokenProvider;
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

/**
 * Spring Security 설정 클래스
 * - JWT 기반 인증 및 권한 설정
 * - UserDetailsService 및 AuthenticationProvider 설정
 */
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    // JWT 토큰을 관리하는 Provider
    private final TokenProvider tokenProvider;

    // Redis를 활용한 인증 정보 저장 (로그아웃 처리 시 사용)
    private final RedisTemplate<String, String> redisTemplate;

    private final JwtSecurityConfig jwtSecurityConfig;

    // Spring Security의 사용자 인증을 위한 서비스 (AdminDetailsService 추가)
    private final AdminDetailsService adminDetailsService;

    /**
     * 비밀번호 암호화를 위한 BCryptPasswordEncoder 등록
     * - 사용자 로그인 시 입력된 비밀번호를 암호화된 비밀번호와 비교할 때 사용
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager 설정
     * - Spring Security의 인증을 처리하는 핵심 매니저
     * - AuthenticationConfiguration을 통해 기본 인증 매니저를 가져옴
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * DaoAuthenticationProvider 설정
     * - UserDetailsService를 활용하여 사용자 정보를 로드
     * - BCryptPasswordEncoder를 사용하여 비밀번호 검증
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(adminDetailsService); // AdminDetailsService에서 사용자 정보 로드
        provider.setPasswordEncoder(passwordEncoder()); // 비밀번호 암호화 설정
        return provider;
    }

    /**
     * Security Filter Chain 설정
     * - JWT 인증 방식을 유지하면서 Spring Security 설정 적용
     */
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
}