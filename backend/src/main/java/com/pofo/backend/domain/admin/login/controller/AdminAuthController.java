package com.pofo.backend.domain.admin.login.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.common.security.dto.TokenDto;
import com.pofo.backend.common.security.jwt.TokenProvider;
import com.pofo.backend.domain.admin.login.dto.AdminLoginRequest;
import com.pofo.backend.domain.admin.login.dto.AdminLoginResponse;
import com.pofo.backend.domain.admin.login.dto.AdminLogoutResponse;
import com.pofo.backend.domain.admin.login.entitiy.Admin;
import com.pofo.backend.domain.admin.login.service.AdminService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
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


            redisTemplate.opsForValue().set(token.getRefreshToken(),"valid",
                    tokenProvider.getRefreshTokenValidationTime(), TimeUnit.MILLISECONDS);

            log.info("🚀 Access Token: {}", token.getAccessToken());
            log.info("🚀 Refresh Token: {}", token.getRefreshToken());
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

    @PostMapping("/logout")
    public ResponseEntity<RsData<AdminLogoutResponse>> logout(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        String token = bearerToken.substring(7);
        // 로그아웃 요청 시, 토큰이 유효하다면 해당 토큰을 Redis에 저장하여 무효화 처리합니다.
        if (tokenProvider.validateToken(token)) {
            long remainingMillis = tokenProvider.getExpiration(token);
            if (remainingMillis > 0) {
                redisTemplate.opsForValue().set(token, "logout", remainingMillis, TimeUnit.MILLISECONDS);
            }
        }
        // 별도 실패 분기를 처리하지 않고, 항상 성공 응답을 반환합니다.
        return ResponseEntity.ok(new RsData<>("200", "성공적으로 로그아웃되었습니다.", new AdminLogoutResponse("성공적으로 로그아웃되었습니다.")));
    }

    /**
     * Refresh Token을 이용해 새로운 Access Token을 발급하는 엔드포인트.
     * 클라이언트는 저장된 Refresh Token을 HTTP 헤더("Refresh-Token")에 담아서 호출합니다.
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<RsData<TokenDto>> refreshToken(
            @RequestHeader(value = "Refresh-Token", required = false) String refreshToken) {

        log.info("Received Refresh-Token: {}", refreshToken);

        try {
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                log.error("Refresh Token is null or empty.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new RsData<>("400", "Refresh Token이 제공되지 않았습니다.", null));
            }

            if (!tokenProvider.validateToken(refreshToken)) {
                log.error("Invalid Refresh Token.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new RsData<>("401", "Refresh Token이 유효하지 않습니다.", null));
            }

            // Refresh Token의 남은 유효 시간을 계산합니다.
            long remainingMillis = tokenProvider.getExpiration(refreshToken);
            final long THREE_DAYS_IN_MILLIS = 3 * 24 * 60 * 60 * 1000L;

            Authentication authentication = tokenProvider.getAuthenticationFromRefreshToken(refreshToken);
            if (authentication == null) {
                log.error("Authentication is null for Refresh Token: {}", refreshToken);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new RsData<>("401", "인증 정보를 가져올 수 없습니다.", null));
            }

            // 새로운 Access Token 발급
            String newAccessToken = tokenProvider.generateAccessToken(authentication);
            if (newAccessToken == null || newAccessToken.trim().isEmpty()) {
                log.error("Generated newAccessToken is null or empty.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new RsData<>("500", "새로운 Access Token을 생성하는 중 오류가 발생했습니다.", null));
            }
            log.info("새로운 Access Token이 발급되었습니다: {}", newAccessToken);

            String newRefreshToken = refreshToken; // 기본값은 기존 Refresh Token 사용

            // 만약 Refresh Token의 남은 유효시간이 3일 이하라면 Refresh Token도 재발급합니다.
            if (remainingMillis <= THREE_DAYS_IN_MILLIS) {
                long now = System.currentTimeMillis();
                newRefreshToken = Jwts.builder()
                        .setSubject(authentication.getName())
                        .setExpiration(new Date(now + tokenProvider.getRefreshTokenValidationTime()))
                        .signWith(tokenProvider.getKey(), SignatureAlgorithm.HS512)
                        .compact();
                // 새로운 Refresh Token을 Redis에도 업데이트합니다.
                redisTemplate.opsForValue().set(newRefreshToken, "valid",
                        tokenProvider.getRefreshTokenValidationTime(), TimeUnit.MILLISECONDS);
                log.info("만료 임박한 Refresh Token을 감지하여 새로운 Refresh Token도 재발급되었습니다: {}", newRefreshToken);
            } else {
                log.info("기존 Refresh Token의 유효기간이 충분하여 재발급하지 않고 기존 Refresh Token을 사용합니다.");
            }

            TokenDto newTokenDto = TokenDto.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .accessTokenValidationTime(tokenProvider.getValidationTime())
                    .refreshTokenValidationTime(tokenProvider.getRefreshTokenValidationTime())
                    .type("Bearer")
                    .build();

            log.info("최종적으로 발급된 TokenDto: {}", newTokenDto);

            return ResponseEntity.ok(new RsData<>("200", "새로운 Access Token이 발급되었습니다.", newTokenDto));

        } catch (Exception e) {
            log.error("Exception occurred in refreshToken: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RsData<>("500", "서버 내부 오류가 발생했습니다.", null));
        }
    }

}