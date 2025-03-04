package com.pofo.backend.domain.user.join.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.user.join.dto.UserJoinRequestDto;
import com.pofo.backend.domain.user.join.dto.UserJoinResponseDto;
import com.pofo.backend.domain.user.join.service.UserJoinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserJoinController {

    private final UserJoinService userJoinService;

    //  회원가입 메서드
    @PostMapping("/join")
    public ResponseEntity<RsData<UserJoinResponseDto>> join (
            @Valid @RequestBody UserJoinRequestDto userJoinRequestDto) {

        //  파라미터 확인용 로깅

        //  회원 가입 서비스 호출
        UserJoinResponseDto response = this.userJoinService.registerUser(userJoinRequestDto);

        // ✅ 응답 코드에 따라 HTTP 상태 코드를 동적으로 설정
        HttpStatus status = response.getResultCode().equals("202") ? HttpStatus.ACCEPTED : HttpStatus.OK;

        return ResponseEntity.status(status)
                .body(new RsData<>(response.getResultCode(),response.getMessage(),response));
    }

    //  계정 통합을 진행하지 않는 경우
    @PostMapping("/join/force")
    public ResponseEntity<RsData<UserJoinResponseDto>> forceRegisterUser(@RequestBody UserJoinRequestDto userJoinRequestDto) {
        UserJoinResponseDto responseDto = userJoinService.forceRegisterUser(userJoinRequestDto);
        return ResponseEntity.ok(new RsData<>("200", "회원가입 완료", responseDto));
    }
}
