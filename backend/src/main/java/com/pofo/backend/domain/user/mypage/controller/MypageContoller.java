package com.pofo.backend.domain.user.mypage.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.user.join.entity.User;
import com.pofo.backend.domain.user.join.repository.UserRepository;
import com.pofo.backend.domain.user.mypage.dto.MypageResponseDto;
import com.pofo.backend.domain.user.mypage.service.MypageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
@RequiredArgsConstructor
public class MypageContoller {
    private final MypageService mypageService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<RsData<User>> getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new RsData<>("401", "로그인이 필요합니다.", null));
        }

        Optional<User> optionalUser = userRepository.findByEmail(authentication.getName());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new RsData<>("404", "유저 정보를 찾을 수 없습니다.", null));
        }

        return ResponseEntity.ok(new RsData<>("200", "유저 정보 조회 성공", optionalUser.get()));
    }


    @GetMapping("/mypage")
    public ResponseEntity<RsData<MypageResponseDto>> getMypage(@RequestParam String email) {
        MypageResponseDto myPageData = mypageService.getMypageData(email);

        return ResponseEntity.ok(new RsData<>("200", "마이페이지 정보 조회 성공", myPageData));
    }
}
