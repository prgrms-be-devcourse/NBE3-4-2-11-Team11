package com.pofo.backend.common.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    // Authorization 헤더의 키값 ("Authorization" 등이 들어감)
    private final String AUTHORIZATION_KEY;
    // JWT 토큰 관련 생성 및 검증 로직을 제공하는 컴포넌트
    private final TokenProvider tokenProvider;
    // Redis를 통해 블랙리스트(로그아웃 처리된 토큰) 여부를 확인하기 위한 템플릿
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String tokenValue = parseHeader(request);
            if (StringUtils.hasText(tokenValue) && tokenProvider.validateToken(tokenValue)) {
                // Redis에서 해당 토큰이 "logout" 상태로 등록되어 있는지 확인
                String logOut = redisTemplate.opsForValue().get(tokenValue);
                if (ObjectUtils.isEmpty(logOut)) {
                    // 토큰으로부터 Authentication 객체를 생성하고 SecurityContext에 저장
                    Authentication authentication = tokenProvider.getAuthentication(tokenValue);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            // 정상적인 경우 다음 필터로 요청 전달
            filterChain.doFilter(request, response);
    }

    /**
     * 요청 헤더에서 Authorization 토큰을 파싱합니다.
     * "Bearer " 접두어가 있으면 제거하고 토큰 문자열만 반환합니다.
     */
    public String parseHeader(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_KEY);
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }
}
