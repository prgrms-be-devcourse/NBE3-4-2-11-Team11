package com.pofo.backend.domain.user.mypage.service;

import com.pofo.backend.domain.user.join.entity.User;
import com.pofo.backend.domain.user.join.repository.UserRepository;
import com.pofo.backend.domain.user.mypage.dto.MypageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class MypageService {
    private final UserRepository userRepository;

    public MypageResponseDto getMypageData(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return MypageResponseDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .sex(user.getSex().name())
                .age(user.getAge().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .jobInterest(user.getJobInterest() != null ? user.getJobInterest() : "정보 없음")
                .userStatus(user.getUserStatus().name())
                .build();
    }
}
