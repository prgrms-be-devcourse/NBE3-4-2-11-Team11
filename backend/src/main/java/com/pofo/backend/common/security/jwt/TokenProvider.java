package com.pofo.backend.common.security.jwt;

import com.pofo.backend.common.security.AdminDetails;
import com.pofo.backend.common.security.AdminDetailsService;
import com.pofo.backend.common.security.CustomUserDetails;
import com.pofo.backend.common.security.dto.TokenDto;
import com.pofo.backend.common.service.CustomUserDetailsService;
import com.pofo.backend.domain.user.join.entity.User;
import com.pofo.backend.domain.user.join.repository.UserRepository;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JWT 토큰 생성 및 검증을 담당하는 컴포넌트입니다.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TokenProvider {

    @Value("${JWT_SECRET_KEY}")
    private String secret;

    @Value("${jwt.expiration.time}")
    private Long validationTime;

    @Value("${AUTHORIZATION_KEY}")
    private String AUTHORIZATION_KEY;

    @Value("${jwt.refresh-token.expiration-time}")
    private Long refreshTokenValidationTime;

    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private SecretKey key;
    private final AdminDetailsService adminDetailsService;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        // Base64 인코딩된 secret을 디코딩하여 SecretKey 생성
        this.key = new SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS512.getJcaName());
    }

    /**
     * 인증 정보를 바탕으로 Access Token과 Refresh Token을 생성합니다.
     *
     * @param authentication 인증 객체
     * @return TokenDto 객체 (토큰 값 및 유효시간 포함)
     */

    public TokenDto createToken(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            log.error("createToken: Authentication 또는 사용자 이름이 null입니다.");
            throw new IllegalArgumentException("Authentication is invalid");
        }

        long now = System.currentTimeMillis();
        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));


        String subject;
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            // 일반 사용자는 이메일을 subject로 사용
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            subject = userDetails.getUsername(); // 여기서 이메일이 리턴됨
        } else if (principal instanceof AdminDetails) {
            // 관리자는 getEmail()을 사용하도록
            AdminDetails adminDetails = (AdminDetails) principal;
            subject = adminDetails.getUsername();
        } else {
            subject = authentication.getName();
        }


        String accessToken = Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date(now + validationTime))
                .claim(AUTHORIZATION_KEY, authorities)
                .signWith(this.key, SignatureAlgorithm.HS512)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(subject)
                .claim(AUTHORIZATION_KEY, authorities)
                .setExpiration(new Date(now + refreshTokenValidationTime))
                .signWith(this.key, SignatureAlgorithm.HS512)
                .compact();

        log.info("Access Token 생성 완료");
        log.info("Refresh Token 생성 완료");

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenValidationTime(validationTime)
                .refreshTokenValidationTime(refreshTokenValidationTime)
                .type("Bearer")
                .build();
    }


    /**
     * 주어진 인증 정보를 바탕으로 Access Token만 생성하여 반환합니다.
     *
     * @param authentication 인증 객체
     * @return 생성된 Access Token 문자열
     */
    public String generateAccessToken(Authentication authentication) {
        return createToken(authentication).getAccessToken();
    }

    /**
     * JWT 토큰을 파싱하여 Authentication 객체를 생성합니다.
     *
     * @param token JWT 토큰 문자열
     * @return Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        Claims claims = parseData(token);
        if (claims == null) {
            throw new IllegalArgumentException("Cannot parse token claims");
        }

        // 권한 정보(claim) 읽기
        String authClaim = claims.get(AUTHORIZATION_KEY, String.class);
        List<SimpleGrantedAuthority> authorities = (authClaim != null && !authClaim.isEmpty())
                ? Arrays.stream(authClaim.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList())
                : Collections.emptyList();

        String subject = claims.getSubject();
        if (subject == null || subject.trim().isEmpty()) {
            log.error("getAuthentication: Subject is null or empty for token: {}", token);
            throw new IllegalArgumentException("Cannot create User with null subject");
        }

        // 만약 권한 목록에 ROLE_ADMIN이 포함되어 있다면, 관리자용 Authentication을 생성
        if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            // 관리자 전용 UserDetailsService(adminDetailsService)를 사용하여 관리자 정보를 조회
            UserDetails adminDetails = adminDetailsService.loadUserByUsername(subject);
            return new UsernamePasswordAuthenticationToken(adminDetails, token, adminDetails.getAuthorities());
        } else {
            // 일반 사용자의 경우, 도메인 User 엔티티를 조회하여 CustomUserDetails로 감싼다.
            User domainUser = userRepository.findByEmail(subject)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + subject));
            CustomUserDetails customUserDetails = new CustomUserDetails(domainUser);
            return new UsernamePasswordAuthenticationToken(customUserDetails, token, customUserDetails.getAuthorities());
        }
    }

    /**
     * Refresh Token을 기반으로 DB에서 사용자 정보를 조회하여 올바른 권한을 포함한 Authentication 객체를 생성합니다.
     *
     * @param token Refresh Token 문자열
     * @return Authentication 객체
     */


    public Authentication getAuthenticationFromRefreshToken(String token) {
        Claims claims = parseData(token);
        String username = claims.getSubject();

        // 토큰에 포함된 권한 정보 읽기
        String authClaim = claims.get(AUTHORIZATION_KEY, String.class);
        List<SimpleGrantedAuthority> authorities = (authClaim != null && !authClaim.isEmpty())
                ? Arrays.stream(authClaim.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList())
                : Collections.emptyList();

        UserDetails userDetails;
        if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            // 관리자 권한이 있는 경우 adminDetailsService를 사용
            userDetails = adminDetailsService.loadUserByUsername(username);
        } else {
            // 일반 사용자의 경우 customUserDetailsService를 사용
            userDetails = customUserDetailsService.loadUserByUsername(username);
        }
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }


    /**
     * 주어진 JWT 토큰의 유효성을 검증합니다.
     *
     * @param token JWT 토큰 문자열
     * @return 토큰이 유효하면 true, 그렇지 않으면 false
     *
     * 2025-02-09 김누리 수정 :
     *   최초 accessToken 재발급을 위해 refreshToken을 Parsing 할 때는 이상이 없지만,
     *   2번째 재발급 시도부터 parsing 과정에서 현재 시간과 만료 시간이 정확히 일치 하기 때문에,
     *   5초 정도의 오차 시간 적용
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .setAllowedClockSkewSeconds(10)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException | SecurityException | ExpiredJwtException |
                 UnsupportedJwtException | IllegalArgumentException e) {
            log.info("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * JWT 토큰을 파싱하여 Claims 객체를 반환합니다.
     *
     * @param token JWT 토큰 문자열
     * @return Claims 객체 또는 파싱 실패 시 null
     */
    public Claims parseData(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(this.key)
                    .setAllowedClockSkewSeconds(10)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.error("Expired token: {}", token);
            return e.getClaims();
        } catch (Exception e) {
            log.error("Failed to parse token: {} | Error: {}", token, e.getMessage());
            return null;
        }
    }

    /**
     * 주어진 Access Token의 남은 유효 시간을 밀리초 단위로 반환합니다.
     *
     * @param accessToken JWT Access Token 문자열
     * @return 남은 유효 시간 (밀리초)
     */
    // Access Token의 남은 만료 시간을  허용 오차 적용하여 계산
    public Long getExpiration(String accessToken) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(key)
                .setAllowedClockSkewSeconds(10)
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }

    public SecretKey getKey() {
        return key;
    }


    public String getRoleFromToken(String token) {
        Claims claims = parseData(token);
        if (claims == null) {
            return null;
        }
        // 토큰에 저장된 권한 문자열(ex: "ROLE_ADMIN,ROLE_USER")을 읽어옵니다.
        String authorities = claims.get(AUTHORIZATION_KEY, String.class);
        if (authorities != null) {
            if (authorities.contains("ROLE_ADMIN")) {
                return "admin";
            } else if (authorities.contains("ROLE_USER")) {
                return "user";
            }
        }
        return null;
    }
}