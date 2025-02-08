package com.pofo.backend.common.security.jwt;

import com.pofo.backend.common.security.AdminDetails;
import com.pofo.backend.common.security.AdminDetailsService;
import com.pofo.backend.common.security.CustomUserDetails;
import com.pofo.backend.common.security.dto.TokenDto;
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
import org.springframework.security.core.userdetails.UserDetailsService;
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

    @Value("${JWT_VALIDATION_TIME}")
    private Long validationTime;

    @Value("${AUTHORIZATION_KEY}")
    private String AUTHORIZATION_KEY;

    @Value("${JWT_REFRESH_VALIDATION_TIME}")
    private Long refreshTokenValidationTime;

    private SecretKey key;
    private final AdminDetailsService adminDetailsService;
    private final UserDetailsService userDetailsService;

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

//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//        String email = userDetails.getUsername();

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
        if (claims == null) {
            throw new IllegalArgumentException("Invalid token claims");
        }
        String username = claims.getSubject();
        log.info("🔍 Refresh Token에서 추출한 사용자 이름: {}", username);

        // refresh token에도 포함된 권한 정보를 읽음
        String authClaim = claims.get(AUTHORIZATION_KEY, String.class);

        if (authClaim != null && authClaim.contains("ROLE_ADMIN")) {
            // 관리자 계정인 경우
            log.info("🔍 관리자 인증 시도: {}", username);
            UserDetails adminDetails = adminDetailsService.loadUserByUsername(username);
            return new UsernamePasswordAuthenticationToken(adminDetails, "", adminDetails.getAuthorities());
        } else {
            // 일반 사용자 계정인 경우
            log.info("🔍 일반 사용자 인증 시도: {}", username); // ✅ 로그 추가
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        }
    }

    /**
     * 주어진 JWT 토큰의 유효성을 검증합니다.
     *
     * @param token JWT 토큰 문자열
     * @return 토큰이 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
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
     * Refresh Token을 기반으로 새로운 Access Token(및 Refresh Token)을 발급하는 메소드입니다.
     *
     * @param refreshToken 기존에 발급된 Refresh Token 문자열
     * @return 새로운 TokenDto 객체 (Access Token 및 Refresh Token 포함)
     */

    public TokenDto refreshAccessToken(String refreshToken) {
        // refresh token의 유효성 검증: 만료되었거나 위변조된 토큰이면 예외 발생
        if (!validateToken(refreshToken)) {
            log.error("refreshAccessToken: 유효하지 않은 refresh token");
            throw new RuntimeException("Refresh token is invalid or expired");
        }

        // refresh token으로부터 Authentication 객체 복원
        Authentication authentication = getAuthenticationFromRefreshToken(refreshToken);

        // 복원된 인증 정보를 기반으로 새로운 Access Token 및 Refresh Token 생성
        TokenDto newTokenDto = createToken(authentication);

        log.info("새로운 Access Token 및 Refresh Token 발급 완료");

        return newTokenDto;
    }

    /**
     * 주어진 Access Token의 남은 유효 시간을 밀리초 단위로 반환합니다.
     *
     * @param accessToken JWT Access Token 문자열
     * @return 남은 유효 시간 (밀리초)
     */
    public Long getExpiration(String accessToken) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }

    public Long getValidationTime() {
        return validationTime;
    }

    public Long getRefreshTokenValidationTime() {
        return refreshTokenValidationTime;
    }

    public SecretKey getKey() {
        return key;
    }

    public long getTokenExpirationTime(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }
}
