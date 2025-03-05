package com.pofo.backend.domain.user.edit.service;

import com.pofo.backend.domain.user.edit.dto.UserProfileUpdateRequestDto;
import com.pofo.backend.domain.user.join.entity.User;
import com.pofo.backend.domain.user.join.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserEditService {

    private final UserRepository userRepository;

    @Transactional
    public void updateUserProfile(String email, UserProfileUpdateRequestDto request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setNickname(request.getNickname());
        user.setJobInterest(request.getJobInterest());
        user.setUserStatus(request.getUserStatusEnum());

        userRepository.save(user);

    }
}
