package com.pofo.backend.common.security.jwt;

import com.pofo.backend.common.security.dto.TokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.annotation.PostConstruct;

import java.util.*;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TokenProvider {
    // 외부 설정 파일이나 환경변수를 통해 주입받는 비밀 문자열 (Base64 인코딩된 값)
    @Value("${JWT_SECRET_KEY}")
    private String secret;

    // Access Token의 유효 시간 (밀리초 단위)
    @Value("${JWT_VALIDATION_TIME}")
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
    }

    // UserDetailsService를 주입받아 DB에서 사용자 정보를 조회하도록 함
    private final UserDetailsService userDetailsService;

    // 생성자 주입 (Lombok @RequiredArgsConstructor 사용 시 final 필드에 대해 자동 주입)
    public TokenProvider(@Value("${JWT_SECRET_KEY}") String secret,
                         @Value("${JWT_VALIDATION_TIME}") Long validationTime,
                         @Value("${AUTHORIZATION_KEY}") String AUTHORIZATION_KEY,
                         @Value("${JWT_REFRESH_VALIDATION_TIME}") Long refreshTokenValidationTime,
                         UserDetailsService userDetailsService) {
        this.secret = secret;
        this.validationTime = validationTime;
        this.AUTHORIZATION_KEY = AUTHORIZATION_KEY;
        this.refreshTokenValidationTime = refreshTokenValidationTime;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Authentication 객체를 기반으로 Access Token과 Refresh Token을 생성합니다.
     *
     * @param authentication 인증 정보를 담고 있는 Authentication 객체
     * @return 생성된 토큰 정보를 담은 TokenDto 객체
     */
    public TokenDto createToken(Authentication authentication) {
        long now = System.currentTimeMillis();

        if (authentication == null || authentication.getName() == null) {
            log.error("createToken: authentication or authentication name is null.");
            throw new IllegalArgumentException("Authentication is invalid");
        }

        String authorities = authentication.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())  // ✅ subject 추가
                .setExpiration(new Date(now + validationTime))
                .claim(AUTHORIZATION_KEY, authorities)
                .signWith(this.key, SignatureAlgorithm.HS512)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())  // ✅ subject 추가
                .setExpiration(new Date(now + refreshTokenValidationTime))
                .signWith(this.key, SignatureAlgorithm.HS512)
                .compact();

        // ✅ 콘솔에 토큰 로그 출력
        log.info("🚀 생성된 Access Token: {}", accessToken);
        log.info("🚀 생성된 Refresh Token: {}", refreshToken);

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenValidationTime(validationTime)
                .refreshTokenValidationTime(refreshTokenValidationTime)
                .type("Bearer")
                .build();
    }

    /**
     * Authentication 객체를 기반으로 Access Token만 생성하여 반환합니다.
     * @param authentication 인증 정보를 담고 있는 Authentication 객체
     * @return 생성된 Access Token 문자열
     */
    public String generateAccessToken(Authentication authentication) {
        return createToken(authentication).getAccessToken();
    }

    /**
     * 토큰을 파싱하여 Authentication 객체를 생성합니다.
     * @param token JWT 토큰 문자열
     * @return 인증 정보를 담은 Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        Claims claims = parseData(token);

        Object authClaim = claims.get(AUTHORIZATION_KEY);
        List<SimpleGrantedAuthority> authorities;
        if (authClaim != null) {
            authorities = Arrays
                    .stream(authClaim.toString().split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } else {

            authorities = Collections.emptyList();
        }

        if (claims.getSubject() == null || claims.getSubject().trim().isEmpty()) {
            log.error("getAuthentication: claims.getSubject() is null or empty. Token: {}", token);
            throw new IllegalArgumentException("Cannot create User with null subject");
        }

        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public Authentication getAuthenticationFromRefreshToken(String token) {
        Claims claims = parseData(token);
        String username = claims.getSubject();
        // DB에서 사용자 정보를 조회하여 UserDetails 객체를 얻음
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
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
            System.out.println("[TokenProvider] Token is valid: " + token);

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
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(this.key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            log.info("Parsed claims: {}", claims);
            System.out.println("[TokenProvider] Parsed claims: " + claims);
            return claims;
        } catch (ExpiredJwtException e) {
            log.error("parseData: Expired token. Token: {}", token);
            return e.getClaims(); // 만료된 경우에도 클레임을 반환하도록 처리
        } catch (Exception e) {
            log.error("parseData: Failed to parse token. Token: {} | Error: {}", token, e.getMessage());
            return null;
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

    // Getter 추가: 클라이언트에 토큰 만료 시간 전달 등 필요시 사용

    public Long getValidationTime() {
        return validationTime;
    }

    public Long getRefreshTokenValidationTime() {
        return refreshTokenValidationTime;
    }

    public SecretKey getKey() {
        return key;
    }
}