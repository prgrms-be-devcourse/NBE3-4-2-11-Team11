package com.pofo.backend.common.security.jwt;

import com.pofo.backend.common.security.dto.TokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.annotation.PostConstruct;

import java.security.SignatureException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TokenProvider {
    // 외부 설정 파일이나 환경변수를 통해 주입받는 비밀 문자열 (Base64 인코딩된 값)
    @Value("${JWT_SECRET_KEY}")
    private String secret;

    // Access Token의 유효 시간 (밀리초 단위)
    @Value("${JWT_VALIDATION_TIME:3600000}")
    private Long validationTime;

    // JWT 내에서 권한 정보를 저장하는 클레임의 키 값 (예: "auth")
    @Value("${AUTHORIZATION_KEY}")
    private String AUTHORIZATION_KEY;

    // Refresh Token의 유효 시간 (밀리초 단위)
    @Value("${JWT_REFRESH_VALIDATION_TIME}")
    private Long refreshTokenValidationTime;

    // 실제 암호화에 사용되는 SecretKey 객체
    private SecretKey key;

    // 객체가 생성된 후 secret 문자열을 디코딩하여 SecretKey를 초기화하는 메서드
    @PostConstruct
    public void init() {
        // secret 문자열을 Base64로 디코딩하고 HS512 알고리즘에 맞는 SecretKeySpec를 생성
        this.key = new SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS512.getJcaName());
        log.info("JWT_VALIDATION_TIME: {}", validationTime);
        log.info("JWT_REFRESH_VALIDATION_TIME: {}", refreshTokenValidationTime);
    }

    /**
     * Authentication 객체를 기반으로 Access Token과 Refresh Token을 생성합니다.
     *
     * @param authentication 인증 정보를 담고 있는 Authentication 객체
     * @return 생성된 토큰 정보를 담은 TokenDto 객체
     */
    public TokenDto createToken(Authentication authentication) {
        // 인증 객체에서 권한(roles) 정보를 콤마(,)로 구분된 문자열로 변환
        String authorities = authentication.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = System.currentTimeMillis();


        Date expirationDate = new Date(now + validationTime);

        // Access Token 생성: subject(사용자 이름)와 권한 정보, 만료 시간을 포함하여 서명
        String accessToken = Jwts.builder()
                .setExpiration(new Date(now + validationTime))  // 만료 시간 설정
                .setSubject(authentication.getName())           // 사용자 이름 설정
                .claim(AUTHORIZATION_KEY, authorities)          // 권한 정보를 클레임에 저장
                .signWith(this.key, SignatureAlgorithm.HS512)     // 비밀 키와 알고리즘을 사용하여 서명
                .compact();

        // Refresh Token 생성: 만료 시간만 포함하여 서명 (보통 사용자 정보는 포함하지 않음)
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + refreshTokenValidationTime))  // 만료 시간 설정
                .signWith(this.key, SignatureAlgorithm.HS512)                 // 비밀 키와 알고리즘을 사용하여 서명
                .compact();

        // 생성된 토큰 정보를 TokenDto 객체에 담아 반환
        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenValidationTime(validationTime)
                .refreshTokenValidationTime(refreshTokenValidationTime)
                .type("Bearer ")
                .build();
    }

    /**
     * 토큰을 파싱하여 Authentication 객체를 생성합니다.
     *
     * @param token JWT 토큰 문자열
     * @return 인증 정보를 담은 Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        // 토큰에서 클레임(데이터)을 파싱
        Claims claims = parseData(token);

        // 클레임에 저장된 권한 정보를 콤마로 구분된 문자열에서 SimpleGrantedAuthority 리스트로 변환
        List<SimpleGrantedAuthority> authorities = Arrays
                .stream(claims.get(AUTHORIZATION_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // 사용자 정보를 기반으로 User 객체 생성 (비밀번호는 빈 문자열로 처리)
        User principal = new User(claims.getSubject(), "", authorities);

        // UsernamePasswordAuthenticationToken 객체를 생성하여 반환
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * JWT 토큰의 유효성을 검증합니다.
     *
     * @param token 검증할 JWT 토큰 문자열
     * @return 유효한 토큰이면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            // 토큰의 서명을 검증 및 파싱 시도
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SignatureException e) {  // 올바른 예외 클래스 사용
            log.info("잘못된 JWT 서명입니다.");
        } catch (SecurityException e) {
            log.info("잘못된 형식의 토큰입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원하지 않는 형식의 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("잘못된 토큰입니다.");
        }
        return false;
    }

    /**
     * JWT 토큰에서 클레임(데이터)를 파싱하여 반환합니다.
     *
     * @param token JWT 토큰 문자열
     * @return 파싱된 클레임 객체
     */
    public Claims parseData(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(this.key)
                    .build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우에도 클레임 데이터를 반환할 수 있음
            return e.getClaims();
        }
    }

    /**
     * Access Token의 남은 유효 시간을 계산하여 반환합니다.
     *
     * @param accessToken JWT Access Token 문자열
     * @return 남은 유효 시간 (밀리초 단위)
     */
    public Long getExpiration(String accessToken) {
        // 토큰에서 만료 날짜를 파싱
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .getExpiration();
        Long now = System.currentTimeMillis();
        return (expiration.getTime() - now);
    }

    /**
     * Authentication 객체를 기반으로 Access Token만 생성하여 반환합니다.zhem wj
     *
     * @param authentication 인증 정보를 담고 있는 Authentication 객체
     * @return 생성된 Access Token 문자열
     */
    public String generateAccessToken(Authentication authentication) {
        return createToken(authentication).getAccessToken();
    }
}
