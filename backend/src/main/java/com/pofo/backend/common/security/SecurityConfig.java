package com.pofo.backend.common.security;


import com.pofo.backend.common.security.jwt.JwtSecurityConfig;
import com.pofo.backend.common.security.jwt.OAuth2AuthenticationSuccessHandler;
import com.pofo.backend.common.security.jwt.TokenProvider;
import com.pofo.backend.domain.user.login.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtSecurityConfig jwtSecurityConfig; // JwtSecurityConfig를 주입
    private final CustomOAuth2UserService customOAuth2UserService; // CustomOAuth2UserService 주입 
    private final OAuth2AuthenticationSuccessHandler successHandler; // OAuth2AuthenticationSuccessHandler 주입

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
}
