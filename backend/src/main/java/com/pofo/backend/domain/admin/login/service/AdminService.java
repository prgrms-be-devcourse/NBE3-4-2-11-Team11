package com.pofo.backend.domain.admin.login.service;

import com.pofo.backend.domain.admin.login.entitiy.Admin;
import com.pofo.backend.domain.admin.login.entitiy.AdminLoginHistory;
import com.pofo.backend.domain.admin.login.repository.AdminRepository;
import com.pofo.backend.domain.admin.login.repository.AdminLoginHistoryRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AdminLoginHistoryRepository adminLoginHistoryRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${ADMIN_USERNAME:admin}")
    private String adminUsername;

    @Value("${ADMIN_PASSWORD:admin_password}")
    private String adminPassword;

    @PostConstruct
    public void initializeAdminUser() {
        if (adminRepository.findByUsername(adminUsername) == null) {
            // 관리자 계정 생성
            Admin admin = Admin.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))  // 비밀번호는 BCrypt로 암호화
                    .status(Admin.Status.ACTIVE)
                    .failureCount(0)
                    .build();

            adminRepository.save(admin);

            // 로그인 이력 생성 (기본적으로 첫 로그인 기록)
            AdminLoginHistory loginHistory = AdminLoginHistory.builder()
                    .admin(admin)  // 생성된 admin과 연결
                    .loginStatus(AdminLoginHistory.SUCCESS)
                    .failureCount(0)
                    .build();

            adminLoginHistoryRepository.save(loginHistory);
        }
    }
}
