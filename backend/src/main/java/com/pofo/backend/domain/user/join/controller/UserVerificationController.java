package com.pofo.backend.domain.user.join.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.user.join.dto.VerifyCodeRequest;
import com.pofo.backend.domain.user.join.service.UserVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserVerificationController {
    private final UserVerificationService userVerificationService;

    /** ✅ 이메일 인증 코드 발송 */
    @PostMapping("/send-verification/{email}")
    public ResponseEntity<RsData<String>> sendVerificationCode(@PathVariable("email") String email) {
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(new RsData<>("400", "이메일 값이 없습니다.", ""));
        }

        String message = userVerificationService.generateAndSendCode(email);

        if (message == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RsData<>("500", "이메일 전송 중 오류 발생", ""));
        }

        return ResponseEntity.ok(new RsData<>("200", message, ""));

    }

    /** ✅ 이메일 인증 코드 검증 */
    @PostMapping("/verify-code")
    public ResponseEntity<RsData<String>> verifyCode(
            @RequestBody VerifyCodeRequest request) {
        boolean isVerified = userVerificationService.verifyCode(
                request.getEmail(),
                request.getCode()
        );

        if (!isVerified) {
            return ResponseEntity.badRequest().body(new RsData<>("400", "인증 코드가 틀렸습니다.", ""));
        }

        userVerificationService.saveOAuthInfo(request.getEmail(), request.getProvider(), request.getIdentify());

        return ResponseEntity.ok(new RsData<>("200", "이메일 인증 완료! 로그인 페이지로 이동합니다.", ""));
    }
}
