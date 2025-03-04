package com.pofo.backend.common.security.jwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class JwtFilterTest {

    private JwtFilter jwtFilter;
    private TokenProvider tokenProvider;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        tokenProvider = mock(TokenProvider.class);
        valueOperations = mock(ValueOperations.class);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
    }

    @Test
    void testValidToken_Authenticated() throws ServletException, IOException {
        // Given: 유효한 JWT 토큰 설정
        String token = "Bearer validToken";
        when(request.getHeader("Authorization")).thenReturn(token);
        when(tokenProvider.validateToken("validToken")).thenReturn(true);
        when(valueOperations.get("validToken")).thenReturn(null); // 로그아웃되지 않음
        Authentication authentication = mock(Authentication.class);
        when(tokenProvider.getAuthentication("validToken")).thenReturn(authentication);

        // When: 필터 실행
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then: SecurityContext에 인증 정보가 저장되었는지 확인
        assertEquals(authentication, SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testInvalidToken_NotAuthenticated() throws ServletException, IOException {
        // Given: 유효하지 않은 JWT 토큰 설정
        String token = "Bearer invalidToken";
        when(request.getHeader("Authorization")).thenReturn(token);
        when(tokenProvider.validateToken("invalidToken")).thenReturn(false);

        // When: 필터 실행
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then: SecurityContext가 설정되지 않아야 함
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testLoggedOutToken_NotAuthenticated() throws ServletException, IOException {
        // Given: 로그아웃된 토큰
        String token = "Bearer loggedOutToken";
        when(request.getHeader("Authorization")).thenReturn(token);
        when(tokenProvider.validateToken("loggedOutToken")).thenReturn(true);
        when(valueOperations.get("loggedOutToken")).thenReturn("logout"); // 로그아웃된 상태

        // When: 필터 실행
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then: SecurityContext가 설정되지 않아야 함
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testNoTokenHeader_NotAuthenticated() throws ServletException, IOException {
        // Given: 요청 헤더에 토큰이 없음
        when(request.getHeader("Authorization")).thenReturn(null);

        // When: 필터 실행
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then: SecurityContext가 설정되지 않아야 함
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testInvalidTokenFormat_NotAuthenticated() throws ServletException, IOException {
        // Given: `Bearer ` 접두사가 없는 토큰
        when(request.getHeader("Authorization")).thenReturn("InvalidTokenFormat");

        // When: 필터 실행
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then: SecurityContext가 설정되지 않아야 함
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
