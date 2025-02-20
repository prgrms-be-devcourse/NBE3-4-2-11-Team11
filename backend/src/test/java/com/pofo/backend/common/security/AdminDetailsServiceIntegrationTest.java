package com.pofo.backend.common.security;


import com.pofo.backend.domain.admin.login.entitiy.Admin;
import com.pofo.backend.domain.admin.login.repository.AdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class AdminDetailsServiceIntegrationTest {

    @Autowired
    private AdminRepository adminRepository;

    private AdminDetailsService adminDetailsService;

    @BeforeEach
    void setUp() {
        adminDetailsService = new AdminDetailsService(adminRepository);

        // 테스트용 관리자 계정 저장
        Admin admin = Admin.builder()
                .username("admin")
                .password("password123")
                .status(Admin.Status.ACTIVE)
                .failureCount(0)
                .build();
        adminRepository.save(admin);
    }

    @Test
    @DisplayName("MySQL에서 관리자 계정을 정상 조회.")
    void testLoadUserByUsername_Success() {
        UserDetails userDetails = adminDetailsService.loadUserByUsername("admin");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("admin");
        assertThat(userDetails.getPassword()).isEqualTo("password123");
    }

    @Test
    @DisplayName("MySQL에서 존재하지 않는 사용자는 예외 발생.")
    void testLoadUserByUsername_NotFound() {
        assertThrows(UsernameNotFoundException.class, () -> {
            adminDetailsService.loadUserByUsername("notfound");
        });
    }
}
