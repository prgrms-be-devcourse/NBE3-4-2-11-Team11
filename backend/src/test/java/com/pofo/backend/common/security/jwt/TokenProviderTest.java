package com.pofo.backend.common.security.jwt;

import com.pofo.backend.common.security.dto.TokenDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class TokenProviderTest {

    private static TokenProvider tokenProvider;

    @BeforeAll
    static void setUp() {
        // TokenProvider는 생성자에서 .env 파일을 로드합니다.
        // 테스트를 위해 프로젝트 루트에 .env 파일을 준비하거나,
        // 시스템 환경 변수를 설정하세요.
        tokenProvider = new TokenProvider();
    }

    @Test
    void testCreateAndValidateToken() {
        String username = "testUser";
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                username,
                "dummyPassword",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        TokenDto tokenDto = tokenProvider.createToken(authentication);
        assertNotNull(tokenDto);
        System.out.println("Generated Access Token: " + tokenDto.getAccessToken());

        boolean isValid = tokenProvider.validateToken(tokenDto.getAccessToken());
        assertTrue(isValid);
        System.out.println("Token is valid: " + isValid);

        Authentication authFromToken = tokenProvider.getAuthentication(tokenDto.getAccessToken());
        assertNotNull(authFromToken);
        System.out.println("Username from Token: " + authFromToken.getName());
    }


    @Test
    void testGetExpiration() {
        String username = "testUser";
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                username,
                "dummyPassword",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // 토큰 생성
        TokenDto tokenDto = tokenProvider.createToken(authentication);
        assertNotNull(tokenDto);
        System.out.println("Generated Access Token: " + tokenDto.getAccessToken());

        // 남은 만료 시간 검증
        Long remainingTime = tokenProvider.getExpiration(tokenDto.getAccessToken());
        assertNotNull(remainingTime);
        System.out.println("Remaining Time (ms): " + remainingTime);

        assertTrue(remainingTime > 0, "Remaining time should be greater than 0");
    }

}
