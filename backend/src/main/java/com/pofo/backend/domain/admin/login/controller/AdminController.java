package com.pofo.backend.domain.admin.login.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.admin.login.entitiy.Admin;
import com.pofo.backend.domain.admin.login.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 현재 로그인한 관리자 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<RsData<Map<String, Object>>> getAdminInfo(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new RsData<>("401", "로그인이 필요합니다.", null));
        }

        Optional<Admin> optionalAdmin = adminService.findByUsername(authentication.getName());
        if (optionalAdmin.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new RsData<>("404", "관리자 정보를 찾을 수 없습니다.", null));
        }

        Admin admin = optionalAdmin.get();
        Map<String, Object> adminData = Map.of(
                "username", admin.getUsername(),
                "status", admin.getStatus().toString()
        );

        return ResponseEntity.ok(new RsData<>("200", "관리자 정보 조회 성공", adminData));
    }
}