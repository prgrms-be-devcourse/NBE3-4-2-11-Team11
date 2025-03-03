package com.pofo.backend.domain.user.logout.service;

import com.pofo.backend.common.security.jwt.TokenProvider;
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

    public UserLogoutResponseDto logout(
            String token,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return UserLogoutResponseDto.builder()
                .message("로그아웃이 완료되었습니다.")
                .resultCode("200")
                .build();
    }
}
