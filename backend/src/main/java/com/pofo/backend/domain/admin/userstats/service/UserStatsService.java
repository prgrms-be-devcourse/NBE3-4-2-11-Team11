package com.pofo.backend.domain.admin.userstats.service;

import com.pofo.backend.domain.admin.userstats.repository.UserStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.stream.Collectors;
import com.pofo.backend.domain.admin.userstats.dto.UserStatsDto;
import com.pofo.backend.domain.user.join.entity.User;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserStatsService {

    private final UserStatsRepository UserStatsRepository;

    public Page<UserStatsDto> getUserStats(LocalDate startDate, LocalDate endDate, User.Sex sex, Pageable pageable) {
        Page<User> usersPage;

        // 조건에 따른 조회 (필터링이 모두 있을 경우, 부분적으로 있을 경우 등)
        if (sex != null && startDate != null && endDate != null) {
            usersPage = UserStatsRepository.findAllBySexAndCreatedAtBetween(sex, startDate, endDate, pageable);
        } else if (sex != null) {
            usersPage = UserStatsRepository.findAllBySex(sex, pageable);
        } else if (startDate != null && endDate != null) {
            usersPage = UserStatsRepository.findAllByCreatedAtBetween(startDate, endDate, pageable);
        } else {
            usersPage = UserStatsRepository.findAll(pageable);
        }

        // User 엔티티를 DTO로 변환
        return usersPage.map(UserStatsDto::new);
    }

    public Page<UserStatsDto> getAllUsers(Pageable pageable) {
        return getUserStats(null, null, null, pageable);
    }

}
