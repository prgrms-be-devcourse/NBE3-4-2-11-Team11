package com.pofo.backend.domain.admin.login.service;

import com.pofo.backend.domain.admin.login.entitiy.Admin;
import com.pofo.backend.domain.admin.login.entitiy.AdminLoginHistory;
import com.pofo.backend.domain.admin.login.repository.AdminRepository;
import com.pofo.backend.domain.admin.login.repository.AdminLoginHistoryRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final AdminLoginHistoryRepository adminLoginHistoryRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${ADMIN_USERNAME:admin}")
    private String adminUsername;

    @Value("${ADMIN_PASSWORD:admin_password}")
    private String adminPassword;

    @PostConstruct
    public void initializeAdminUser() {
        if (adminRepository.findByUsername(adminUsername).isEmpty()) {
            Admin admin = Admin.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .status(Admin.Status.ACTIVE)
                    .failureCount(0)
                    .build();
            adminRepository.save(admin);

            AdminLoginHistory loginHistory = AdminLoginHistory.builder()
                    .admin(admin)
                    .loginStatus(AdminLoginHistory.SUCCESS)
                    .failureCount(0)
                    .build();
            adminLoginHistoryRepository.save(loginHistory);
        }
    }


    @Transactional
    public void recordLoginFailure(String username) {
        Optional<Admin> optionalAdmin = adminRepository.findByUsername(username);
        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();

            // 실패 횟수가 5 이상이면 더 이상 증가하지 않음
            if (admin.getFailureCount() >= 5) {
                return;
            }

            int newFailureCount = admin.getFailureCount() + 1;
            admin.setFailureCount(newFailureCount);

            if (newFailureCount >= 5) {
                admin.setStatus(Admin.Status.INACTIVE);
            }

            adminRepository.save(admin);

            AdminLoginHistory loginHistory = AdminLoginHistory.builder()
                    .admin(admin)
                    .loginStatus(AdminLoginHistory.FAILED)
                    .failureCount(newFailureCount)
                    .build();
            adminLoginHistoryRepository.save(loginHistory);

        } else {
        }
    }

    @Transactional
    public void recordLoginSuccess(String username) {
        Optional<Admin> optionalAdmin = adminRepository.findByUsername(username);
        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
            admin.setFailureCount(0);
            adminRepository.save(admin);

            AdminLoginHistory loginHistory = AdminLoginHistory.builder()
                    .admin(admin)
                    .loginStatus(AdminLoginHistory.SUCCESS)
                    .failureCount(0)
                    .build();
            adminLoginHistoryRepository.save(loginHistory);
        }
    }

    // 관리자 조회 메서드 추가
    public Optional<Admin> findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }
}
