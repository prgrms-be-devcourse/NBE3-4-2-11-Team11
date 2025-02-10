package com.pofo.backend.domain.user.logout.service;
//
//import com.pofo.backend.common.security.jwt.TokenProvider;
//import com.pofo.backend.common.service.TokenBlacklistService;
//import com.pofo.backend.domain.user.logout.dto.UserLogoutResponseDto;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class UserLogoutService {
//
//    private final TokenProvider tokenProvider;
//    private final TokenBlacklistService tokenBlacklistService;
//
//
//    public UserLogoutResponseDto logout(
//            String token,
//            HttpServletRequest request,
//            HttpServletResponse response
//    ) {
//        log.info("UserLogoutService.logout({}, {})", token, request);
//
//        //  로그아웃 처리 (클라이언트에 적재된 토큰 삭제)
//        if (tokenProvider.validateToken(token)) {
//            long expirationTime = tokenProvider.getTokenExpirationTime(token);
//            tokenBlacklistService.addToBlacklist(token, expirationTime);
//        }
//
//        //  클라이언트 세션 무효화
//        request.getSession().invalidate();
//
//        return UserLogoutResponseDto.builder()
//                .message("로그아웃이 완료 되었습니다.")
//                .resultCode("200")
//                .build();
//    }
//}

import com.pofo.backend.common.security.jwt.TokenProvider;
import com.pofo.backend.common.service.TokenBlacklistService;
import com.pofo.backend.domain.user.logout.dto.UserLogoutResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLogoutService {

    private final TokenProvider tokenProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private final RedisTemplate<String, String> redisTemplate;

    public UserLogoutResponseDto logout(
            String token,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        log.info("🚪 로그아웃 요청 받음: Token = {}", token);

        // ✅ Access Token 유효성 검사
        if (tokenProvider.validateToken(token)) {
            long expirationTime = tokenProvider.getTokenExpirationTime(token);

            // ✅ Access Token을 블랙리스트에 추가
            if (expirationTime > 0) {
                log.info("🛑 Access Token 블랙리스트 추가 (TTL: {} 초)", expirationTime / 1000);
                tokenBlacklistService.addToBlacklist(token, expirationTime);
            }
        } else {
            log.warn("❌ 유효하지 않은 Access Token으로 로그아웃 요청");
        }

        // ✅ Redis에서 해당 사용자의 Refresh Token 삭제 (완전한 로그아웃)
        String refreshTokenKey = "refresh_token:" + token;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(refreshTokenKey))) {
            redisTemplate.delete(refreshTokenKey);
            log.info("✅ Redis에서 Refresh Token 삭제 완료");
        } else {
            log.warn("⚠️ 해당 사용자의 Refresh Token이 Redis에 존재하지 않음");
        }

        // ✅ 클라이언트 세션 무효화
        request.getSession().invalidate();
        log.info("🔓 클라이언트 세션 무효화 완료");

        return UserLogoutResponseDto.builder()
                .message("로그아웃이 완료되었습니다.")
                .resultCode("200")
                .build();
    }
}