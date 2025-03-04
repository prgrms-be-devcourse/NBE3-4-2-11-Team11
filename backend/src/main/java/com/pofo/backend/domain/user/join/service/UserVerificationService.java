package com.pofo.backend.domain.user.join.service;

import com.pofo.backend.domain.user.join.entity.Oauth;
import com.pofo.backend.domain.user.join.entity.User;
import com.pofo.backend.domain.user.join.repository.OauthRepository;
import com.pofo.backend.domain.user.join.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserVerificationService {
    private final MailService mailService;
    private final UserRepository userRepository;
    private final OauthRepository oauthRepository;

    // ✅ 간단한 메모리 저장소 (Redis 대신)
    private static final ConcurrentHashMap<String, String> verificationCodes = new ConcurrentHashMap<>();

    /** ✅ 인증 코드 생성 & 이메일 발송 */
    public String generateAndSendCode(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("이메일 값이 없습니다.");
        }

        String code = generateRandomCode();
        verificationCodes.put(email, code);

        try {
            mailService.sendVerificationEmail(email, code);
        } catch (Exception e) {
            return "이메일 전송 중 오류 발생";
        }

        return code;
    }

    /** ✅ 인증 코드 검증 */
    public boolean verifyCode(String email, String inputCode) {
        String correctCode = verificationCodes.get(email);

        if (correctCode != null && correctCode.equals(inputCode)) {
            return true;
        }
        return false;
    }

    /** ✅ 6자리 랜덤 코드 생성 */
    private String generateRandomCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    /** ✅ 이메일 인증 후 OAuth 정보 저장 */
    public void saveOAuthInfo(String email, String rawProvider, String identify) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 유저가 존재하지 않습니다."));

    // ✅ Enum으로 변환
        Oauth.Provider provider;
        try {
            provider = Oauth.Provider.valueOf(rawProvider.toUpperCase());

        } catch (IllegalArgumentException  e) {
            throw new RuntimeException(e);
        }

        Oauth oauth = Oauth.builder()
                .user(user)
                .provider(provider)
                .identify(identify)
                .build();
        oauthRepository.save(oauth);

        }

    }
