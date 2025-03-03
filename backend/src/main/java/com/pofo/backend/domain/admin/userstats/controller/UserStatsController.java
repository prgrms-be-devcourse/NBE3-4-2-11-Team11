package com.pofo.backend.domain.admin.userstats.controller;

import com.pofo.backend.domain.admin.userstats.dto.UserStatsDto;
import com.pofo.backend.domain.admin.userstats.service.UserStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/userstats")
@RequiredArgsConstructor
public class UserStatsController {

    private final UserStatsService userStatsService;

    @GetMapping
    public ResponseEntity<List<UserStatsDto>> getAllUsers() {
        // Pageable.unpaged()를 사용해 전체 데이터를 페이징 없이 조회하고,
        // 반환된 Page 객체에서 List를 추출합니다.
        Page<UserStatsDto> page = userStatsService.getAllUsers(Pageable.unpaged());
        List<UserStatsDto> userList = page.getContent();
        return ResponseEntity.ok(userList);
    }
}
