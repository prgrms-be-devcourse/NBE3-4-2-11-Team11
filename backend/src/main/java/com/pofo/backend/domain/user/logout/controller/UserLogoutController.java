package com.pofo.backend.domain.user.logout.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.user.logout.dto.UserLogoutResponseDto;
import com.pofo.backend.domain.user.logout.service.UserLogoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserLogoutController {

    private final UserLogoutService userLogoutService;

    @PostMapping("/logout")
    public ResponseEntity<RsData<UserLogoutResponseDto>> logout(
            @RequestHeader("Authorization") String authorization,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ) {
        String token = authorization.replace("Bearer ", "");
        UserLogoutResponseDto responseDto = userLogoutService.logout(
                token,
                httpServletRequest,
                httpServletResponse);


        return ResponseEntity.ok(
                new RsData<>(responseDto.getResultCode(), responseDto.getMessage(), responseDto));
    }
}