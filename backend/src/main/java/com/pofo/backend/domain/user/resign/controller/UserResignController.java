package com.pofo.backend.domain.user.resign.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.common.security.cookie.TokenCookieUtil;
import com.pofo.backend.domain.user.resign.dto.UserResignResponseDto;
import com.pofo.backend.domain.user.resign.service.UserResignService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/v1/user")
public class UserResignController {
    private final UserResignService userResignService;
    private final TokenCookieUtil tokenCookieUtil;

    @DeleteMapping("/resign")
    public ResponseEntity<RsData<UserResignResponseDto>> resignUser(@RequestBody Map<String, String> requestBody, HttpServletResponse response) {
        log.info("DELETE 요청 도착!");
        String email = requestBody.get("email"); // ✅ body에서 email 가져오기
        log.info("📩 이메일 주소 수신: {}", email);

        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new RsData<>("401", "로그인이 필요합니다."));
        }

        userResignService.deleteUserByEmail(email);
        tokenCookieUtil.cleanTokenCookies(response,"accessCookie");
        tokenCookieUtil.cleanTokenCookies(response,"refreshCookie");
        tokenCookieUtil.cleanTokenCookies(response,"lastLoginProvider");

        return ResponseEntity.ok(new RsData<>("200", "회원 탈퇴 완료"));
    }
}
