package com.pofo.backend.common.security.jwt;

import com.pofo.backend.common.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final TokenBlacklistService tokenBlacklistService; // 추가

    public void configure(HttpSecurity http) throws Exception {
//        http.addFilterBefore(new JwtFilter("Authorization", tokenProvider, redisTemplate), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new JwtFilter("Authorization", tokenProvider, tokenBlacklistService), UsernamePasswordAuthenticationFilter.class);

    }
}