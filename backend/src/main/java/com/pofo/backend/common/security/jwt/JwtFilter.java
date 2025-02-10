package com.pofo.backend.common.security.jwt;//package com.pofo.backend.common.security.jwt;
//
//import com.pofo.backend.common.service.TokenBlacklistService;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//import java.io.IOException;
//
//import org.springframework.data.redis.core.RedisTemplate;
//import java.util.concurrent.TimeUnit;
//
//@RequiredArgsConstructor
//public class JwtFilter extends OncePerRequestFilter {
//
//    private final String AUTHORIZATION_KEY;
//    private final TokenProvider tokenProvider;
//    private final TokenBlacklistService tokenBlacklistService;
//    private final RedisTemplate<String, String> redisTemplate; // ✅ Redis 추가
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//        String tokenValue = parseHeader(request);
//
//        if (StringUtils.hasText(tokenValue) && tokenProvider.validateToken(tokenValue)) {
//            // ✅ 1. 블랙리스트에 포함된 토큰인지 확인
//            if (tokenBlacklistService.isBlacklisted(tokenValue)) {
//                SecurityContextHolder.clearContext();
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그아웃된 토큰입니다.");
//                return;
//            }
//
//            // ✅ 2. Redis에서 Access Token이 존재하는지 확인
//            String redisKey = "access_token:" + tokenValue;
//            if (Boolean.FALSE.equals(redisTemplate.hasKey(redisKey))) {
//                SecurityContextHolder.clearContext();
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 만료되었습니다. 다시 로그인하세요.");
//                return;
//            }
//
//            // ✅ 3. 정상적인 Access Token이면 SecurityContext에 저장
//            Authentication authentication = tokenProvider.getAuthentication(tokenValue);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//        filterChain.doFilter(request, response);
//    }
//
//    private String parseHeader(HttpServletRequest request) {
//        String token = request.getHeader(AUTHORIZATION_KEY);
//        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
//            return token.substring(7);
//        }
//        return null;
//    }
//}



import com.pofo.backend.common.security.dto.TokenDto;
import com.pofo.backend.common.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final String AUTHORIZATION_KEY;
    private final TokenProvider tokenProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String tokenValue = parseHeader(request);

        if (StringUtils.hasText(tokenValue)) {
            // ✅ 1. Access Token 유효성 검사
            if (!tokenProvider.validateToken(tokenValue)) {
                log.warn("❌ Access Token 만료됨: {}", tokenValue);

                // ✅ 2. Refresh Token 확인 (Redis에서 해당 사용자의 Refresh Token 조회)
                String identifier = getUserIdentifier(tokenValue);
                String refreshTokenKey = getRefreshTokenKey(tokenValue, identifier);

                String storedRefreshToken = redisTemplate.opsForValue().get(refreshTokenKey);

                if (storedRefreshToken == null || !tokenProvider.validateToken(storedRefreshToken)) {
                    // ✅ 3. Refresh Token도 없거나 만료되었으면 강제 로그아웃 처리
                    log.warn("🚨 Refresh Token도 없음. 사용자 강제 로그아웃: {}", identifier);
                    SecurityContextHolder.clearContext();
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "세션이 만료되었습니다. 다시 로그인하세요.");
                    return;
                }

                // ✅ 4. Refresh Token이 유효하면 새로운 Access Token 발급
                log.info("🔄 Refresh Token을 사용해 새로운 Access Token 발급: {}", identifier);
                TokenDto newTokenDto = tokenProvider.refreshAccessToken(storedRefreshToken);

                // ✅ 5. 새 Access Token을 응답 헤더에 추가
                response.setHeader("Authorization", "Bearer " + newTokenDto.getAccessToken());
                response.setHeader("Refresh-Token", newTokenDto.getRefreshToken());

                // ✅ 6. 새롭게 발급된 Access Token을 Redis에 저장
                redisTemplate.opsForValue().set(
                        getAccessTokenKey(tokenValue, identifier),
                        identifier,
                        tokenProvider.getValidationTime(),
                        TimeUnit.MILLISECONDS
                );

                // ✅ 7. 새로운 Access Token을 SecurityContextHolder에 저장
                Authentication authentication = tokenProvider.getAuthentication(newTokenDto.getAccessToken());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // ✅ 8. 정상적인 Access Token이면 SecurityContext에 저장
                if (!tokenBlacklistService.isBlacklisted(tokenValue)) {
                    Authentication authentication = tokenProvider.getAuthentication(tokenValue);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 요청 헤더에서 Authorization 토큰을 파싱합니다.
     */
    private String parseHeader(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_KEY);
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    /**
     * Access Token을 저장할 Redis 키 반환
     */
    private String getAccessTokenKey(String token, String identifier) {
        if (isAdminToken(token)) {
            return "admin_access_token:" + identifier;
        } else {
            return "user_access_token:" + identifier;
        }
    }

    /**
     * Refresh Token을 저장할 Redis 키 반환
     */
    private String getRefreshTokenKey(String token, String identifier) {
        if (isAdminToken(token)) {
            return "admin_refresh_token:" + identifier;
        } else {
            return "user_refresh_token:" + identifier;
        }
    }

    /**
     * 토큰이 Admin용인지 확인
     */
    private boolean isAdminToken(String token) {
        return tokenProvider.getAuthentication(token).getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * Admin과 User의 구별을 위해 ID(identifier) 반환
     * - User는 email
     * - Admin은 username
     */
    private String getUserIdentifier(String token) {
        Authentication authentication = tokenProvider.getAuthentication(token);
        if (isAdminToken(token)) {
            return authentication.getName(); // Admin은 username
        } else {
            return tokenProvider.getEmailFromToken(token); // User는 email
        }
    }
}
