package com.pofo.backend.domain.user.resign.service;

import com.pofo.backend.domain.user.join.entity.User;
import com.pofo.backend.domain.user.join.repository.OauthRepository;
import com.pofo.backend.domain.user.join.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserResignService {
    private final UserRepository userRepository;
    private final OauthRepository oauthRepository;

    @Transactional
    public void deleteUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        User user = userOptional.get();

        // ✅ OAuth 정보 먼저 삭제 (외래키 문제 방지)
        oauthRepository.deleteByUser(user);

        // ✅ 유저 삭제
        userRepository.delete(user);
    }
}
