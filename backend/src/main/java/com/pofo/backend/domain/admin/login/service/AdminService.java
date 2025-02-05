package com.pofo.backend.domain.admin.login.service;

import com.pofo.backend.domain.admin.login.entitiy.Admin;
import com.pofo.backend.domain.admin.login.entitiy.AdminLoginHistory;
import com.pofo.backend.domain.admin.login.repository.AdminRepository;
import com.pofo.backend.domain.admin.login.repository.AdminLoginHistoryRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
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
                    .admin(admin)
                    .loginStatus(AdminLoginHistory.SUCCESS)
                    .failureCount(0)
                    .build();

            adminLoginHistoryRepository.save(loginHistory);
        }
    }

    /**
     * 로그인 실패 시 실패 횟수를 증가시키고, 5회 이상이면 계정을 잠금 처리한 후 실패 이력을 기록합니다.
     *
     * @param username 로그인 시도한 관리자 아이디
     */
    @Transactional
    public void recordLoginFailure(String username) {
        Optional<Admin> optionalAdmin = adminRepository.findByUsername(username);
        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
            // 실패 횟수 증가
            int newFailureCount = admin.getFailureCount() + 1;
            admin.setFailureCount(newFailureCount);

            // 5회 이상 실패 시 계정을 비활성화(잠금)
            if (newFailureCount >= 5) {
                admin.setStatus(Admin.Status.INACTIVE);
            }
            adminRepository.save(admin);

            // 로그인 실패 이력 저장
            AdminLoginHistory loginHistory = AdminLoginHistory.builder()
                    .admin(admin)
                    .loginStatus(AdminLoginHistory.FAILED)
                    .failureCount(newFailureCount)
                    .build();
            adminLoginHistoryRepository.save(loginHistory);
        }
    }

    /**
     * 로그인 성공 시 실패 횟수를 초기화하고 성공 이력을 기록합니다.
     *
     * @param username 로그인에 성공한 관리자 아이디
     */
    @Transactional
    public void recordLoginSuccess(String username) {
        Optional<Admin> optionalAdmin = adminRepository.findByUsername(username);
        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
            // 로그인 성공 시 실패 횟수 초기화
            admin.setFailureCount(0);


            adminRepository.save(admin);

            // 로그인 성공 이력 저장
            AdminLoginHistory loginHistory = AdminLoginHistory.builder()
                    .admin(admin)
                    .loginStatus(AdminLoginHistory.SUCCESS)
                    .failureCount(0)
                    .build();
            adminLoginHistoryRepository.save(loginHistory);
        }
    }
}
