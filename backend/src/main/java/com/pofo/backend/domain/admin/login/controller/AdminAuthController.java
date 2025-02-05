package com.pofo.backend.domain.admin.login.controller;

import com.pofo.backend.common.base.Empty;
import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.common.security.dto.TokenDto;
import com.pofo.backend.common.security.jwt.TokenProvider;
import com.pofo.backend.domain.admin.login.dto.AdminLoginRequest;
import com.pofo.backend.domain.admin.login.dto.AdminLoginResponse;
import com.pofo.backend.domain.admin.login.entitiy.Admin;
import com.pofo.backend.domain.admin.login.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final AdminService adminService;
    private final RedisTemplate<String, String> redisTemplate;
    // 컨트롤러에서 암호 비교를 위해 passwordEncoder 주입 (또는 adminService 내부 메서드 활용)
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<RsData<AdminLoginResponse>> login(@RequestBody AdminLoginRequest request) {
        try {
            // 인증 시도
            Authentication authentication = authenticationManager.authenticate(request.getAuthenticationToken());

            // 로그인 성공 시 실패 이력 초기화
            adminService.recordLoginSuccess(request.getUsername());

            // JWT 토큰 생성
            TokenDto token = tokenProvider.createToken(authentication);

            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + token.getAccessToken())
                    .header("Refresh-Token", token.getRefreshToken())
                    .body(new RsData<>("200", "로그인 성공", new AdminLoginResponse("로그인 성공")));
        } catch (AuthenticationException e) {
            // 아이디에 해당하는 관리자 정보를 조회
            Optional<Admin> optionalAdmin = adminService.findByUsername(request.getUsername());
            if (optionalAdmin.isPresent()) {
                Admin admin = optionalAdmin.get();
                // 계정이 비활성화 상태라면
                if (admin.getStatus() == Admin.Status.INACTIVE) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new RsData<>("401", "계정이 비활성화 상태입니다.",
                                    new AdminLoginResponse("계정이 비활성화 상태입니다.")));
                } else {
                    // 아이디는 맞으나 비밀번호가 일치하지 않는 경우
                    // (여기서 passwordEncoder를 통해 입력한 비밀번호와 DB의 암호화된 비밀번호를 비교)
                    if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                        // 로그인 실패 이력 증가
                        adminService.recordLoginFailure(request.getUsername());
                        // 최신 실패 횟수 조회 (실패 기록이 바로 반영되지 않을 경우를 대비)
                        admin = adminService.findByUsername(request.getUsername()).orElse(admin);
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(new RsData<>("401",
                                        "아이디가 맞은경우 비밀번호가 일치하지 않습니다 (틀린회수 " + admin.getFailureCount() + "회)",
                                        new AdminLoginResponse("비밀번호 불일치")));
                    }
                }
            }
            // 아이디 자체가 존재하지 않는 경우 혹은 그 외
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new RsData<>("401", "아이디 및 비밀번호가 일치하지 않습니다.",
                            new AdminLoginResponse("로그인 실패")));
        }
    }

    // 로그아웃 엔드포인트는 그대로…
    @PostMapping("/logout")
    public ResponseEntity<RsData<Empty>> logout(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            if (tokenProvider.validateToken(token)) {
                long remainingMillis = tokenProvider.getExpiration(token) - System.currentTimeMillis();
                redisTemplate.opsForValue().set(token, "logout", remainingMillis, TimeUnit.MILLISECONDS);
            }
        }
        return ResponseEntity.ok(new RsData<>("200", "로그아웃 성공"));
    }
}
