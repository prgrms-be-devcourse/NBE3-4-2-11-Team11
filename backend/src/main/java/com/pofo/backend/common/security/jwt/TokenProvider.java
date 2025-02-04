package com.pofo.backend.common.security.jwt;

import com.pofo.backend.common.security.dto.TokenDto;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TokenProvider {
    private final String AUTHORIZATION_KEY;
    private final Long validationTime;
    private final Long refreshTokenValidationTime;
    private final SecretKey key;

    public TokenProvider() {
        // .env 파일에서 환경 변수 읽기
        Dotenv dotenv = Dotenv.load();
        String secret = dotenv.get("JWT_SECRET");
        this.validationTime = Long.parseLong(dotenv.get("JWT_VALIDATION_TIME")) * 1000;  // 1시간
        this.refreshTokenValidationTime = Long.parseLong(dotenv.get("JWT_REFRESH_VALIDATION_TIME")) * 1000;  // 2시간
        this.AUTHORIZATION_KEY = dotenv.get("AUTHORIZATION_KEY");
        // 비밀 키 설정
        this.key = new SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS512.getJcaName());
    }

    // Authentication 객체를 통해 토큰 생성
    public TokenDto createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = System.currentTimeMillis();

        String accessToken = Jwts.builder()
                .setExpiration(new Date(now + validationTime))
                .setSubject(authentication.getName())
                .claim(AUTHORIZATION_KEY, authorities)
                .signWith(this.key, SignatureAlgorithm.HS512)
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + refreshTokenValidationTime))
                .signWith(this.key, SignatureAlgorithm.HS512)
                .compact();

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenValidationTime(validationTime)
                .refreshTokenValidationTime(refreshTokenValidationTime)
                .type("Bearer ")
                .build();
    }

    // 토큰을 통해 Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        Claims claims = parseData(token);

        List<SimpleGrantedAuthority> authorities = Arrays
                .stream(claims.get(AUTHORIZATION_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException | SecurityException e) {
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

    public Claims parseData(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(this.key)
                    .build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public Long getExpiration(String accessToken) {
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody().getExpiration();
        Long now = System.currentTimeMillis();
        return (expiration.getTime() - now);
    }
}
