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
