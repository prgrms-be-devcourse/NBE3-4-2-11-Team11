package com.pofo.backend.domain.user.logout.service;

import com.pofo.backend.common.security.jwt.TokenProvider;
import com.pofo.backend.common.service.TokenBlacklistService;
import com.pofo.backend.domain.user.logout.dto.UserLogoutResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLogoutService {

    private final TokenProvider tokenProvider;
    private final TokenBlacklistService tokenBlacklistService;


    public UserLogoutResponseDto logout(
            String token,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        log.info("UserLogoutService.logout({}, {})", token, request);

        //  로그아웃 처리 (클라이언트에 적재된 토큰 삭제)
        if (tokenProvider.validateToken(token)) {
            long expirationTime = tokenProvider.getTokenExpirationTime(token);
            tokenBlacklistService.addToBlacklist(token, expirationTime);
        }

        //  클라이언트 세션 무효화
        request.getSession().invalidate();

        return UserLogoutResponseDto.builder()
                .message("로그아웃이 완료 되었습니다.")
                .resultCode("200")
                .build();
    }
}
