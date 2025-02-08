package com.pofo.backend.common.security.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.pofo.backend.common.security.AdminDetailsService;
import com.pofo.backend.common.security.CustomUserDetails;
import com.pofo.backend.domain.user.join.entity.User;
import com.pofo.backend.domain.user.join.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class TokenProviderTest {

    @InjectMocks
    private TokenProvider tokenProvider;

    @Mock
    private UserRepository usersRepository;

    @Mock
    private AdminDetailsService adminDetailsService;

    @Mock
    private UserDetailsService userDetailsService;

    // 테스트에 사용할 프로퍼티 값
    private final String secret = Base64.getEncoder().encodeToString("YxGFS1/HFxZejbF1UZkExiRnq30YiWa75ljM6aNzruym5mFjd7yM1kdbtbu5mf53NPGMVTMWOBn4bOyTGarKMQ".getBytes());
    private final Long validationTime = 3600000L; // 1시간
    private final Long refreshTokenValidationTime = 7200000L; // 2시간
    private final String AUTHORIZATION_KEY = "auth";

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(tokenProvider, "secret", secret);
        ReflectionTestUtils.setField(tokenProvider, "validationTime", validationTime);
        ReflectionTestUtils.setField(tokenProvider, "refreshTokenValidationTime", refreshTokenValidationTime);
        ReflectionTestUtils.setField(tokenProvider, "AUTHORIZATION_KEY", AUTHORIZATION_KEY);

        // 추가: usersRepository와 adminDetailsService 주입
        ReflectionTestUtils.setField(tokenProvider, "usersRepository", usersRepository);
        ReflectionTestUtils.setField(tokenProvider, "adminDetailsService", adminDetailsService);

        tokenProvider.init();
    }


    @Test
    public void testGetAuthentication_User() {
        // Arrange
        String email = "user@example.com";
        // User 엔티티를 빌더 패턴을 이용하여 생성 (빌더가 없는 경우, 공개 생성자나 팩토리 메서드를 사용)
        User user = User.builder()
                .email(email)
                // 필요한 다른 필드도 설정
                .build();

        // UsersRepository가 이메일로 User를 반환하도록 설정
        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // JWT 토큰 생성 (subject: 이메일, 권한: ROLE_USER)
        long now = System.currentTimeMillis();
        String token = Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(now + validationTime))
                .claim(AUTHORIZATION_KEY, "ROLE_USER")
                .signWith(tokenProvider.getKey(), SignatureAlgorithm.HS512)
                .compact();

        // Act
        Authentication authentication = tokenProvider.getAuthentication(token);

        // Assert
        assertNotNull(authentication, "Authentication 객체는 null이면 안됩니다.");
        assertTrue(authentication.getPrincipal() instanceof CustomUserDetails, "Principal은 CustomUserDetails 타입이어야 합니다.");

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        assertEquals(email, userDetails.getUsername(), "토큰의 subject와 UserDetails의 username이 동일해야 합니다.");
        assertEquals(user, userDetails.getUser(), "내부의 도메인 User 객체가 일치해야 합니다.");
    }
}
