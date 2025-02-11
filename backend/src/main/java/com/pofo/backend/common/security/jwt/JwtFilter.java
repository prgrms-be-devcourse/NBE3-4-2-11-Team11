package com.pofo.backend.common.security.jwt;

import com.pofo.backend.common.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    // Authorization 헤더의 키값 ("Authorization" 등이 들어감)
    private final String AUTHORIZATION_KEY;
    // JWT 토큰 관련 생성 및 검증 로직을 제공하는 컴포넌트
    private final TokenProvider tokenProvider;
    // TokenBlacklistService를 통해 로그아웃 처리된 토큰 여부 확인
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String tokenValue = parseHeader(request);

        if (StringUtils.hasText(tokenValue) && tokenProvider.validateToken(tokenValue)) {
            // 블랙리스트에 포함되었는지 TokenBlacklistService를 통해 확인
            if (!tokenBlacklistService.isBlacklisted(tokenValue)) {
                Authentication authentication = tokenProvider.getAuthentication(tokenValue);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
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