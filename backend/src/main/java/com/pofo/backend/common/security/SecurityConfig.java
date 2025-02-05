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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


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
}



*/


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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtSecurityConfig jwtSecurityConfig;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler successHandler;

    // 관리자 전용 UserDetailsService
    private final AdminDetailsService adminDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 관리자용 SecurityFilterChain
     * - '/api/v1/admin/**' 경로에 대해 별도의 보안 설정 적용
     */
    @Bean
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        // JWT 필터 등 추가 설정이 필요하면 jwtSecurityConfig.configure(http) 호출 가능
        jwtSecurityConfig.configure(http);

        http
                // '/api/v1/admin/**' 경로에만 적용
                .securityMatcher("/api/v1/admin/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 관리자 로그인은 인증 없이 접근 가능하도록 허용 (필요 시)
                        .requestMatchers("/api/v1/admin/login").permitAll()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider()); // 관리자 전용 provider 사용
        return http.build();
    }

    /**
     * 유저용 SecurityFilterChain
     * - '/api/v1/user/**' 경로에 대해 별도의 보안 설정 적용
     */
    @Bean
    public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {
        // JWT 필터 등 추가 설정이 필요하면 jwtSecurityConfig.configure(http) 호출 가능
        jwtSecurityConfig.configure(http);

        http
                // '/api/v1/user/**' 경로에만 적용
                .securityMatcher("/api/v1/user/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 유저 로그인, OAuth2 로그인은 인증 없이 접근 가능하도록 허용
                        .requestMatchers("/api/v1/user/login", "/api/v1/user/naver/login", "/api/v1/user/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                )
                // OAuth2 로그인 설정 (필요 시)
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(auth -> auth
                                .baseUri("/api/v1/user/oauth2/authorize")
                        )
                        .redirectionEndpoint(redir -> redir
                                .baseUri("/api/v1/user/oauth2/callback/*")
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(successHandler)
                );
        return http.build();
    }

    /**
     * 관리자용 DaoAuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(adminDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * AuthenticationManager 빈이 필요한 경우 등록
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
