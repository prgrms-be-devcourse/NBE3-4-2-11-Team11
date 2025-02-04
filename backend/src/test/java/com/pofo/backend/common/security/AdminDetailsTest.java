package com.pofo.backend.common.security;


import com.pofo.backend.domain.admin.login.entitiy.Admin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class AdminDetailsTest {

    private Admin admin;
    private AdminDetails adminDetails;

    @BeforeEach
    void setUp() {
        admin = Admin.builder()
                .id(1L)
                .username("admin")
                .password("password123")
                .status(Admin.Status.ACTIVE)
                .failureCount(0)
                .build();

        adminDetails = new AdminDetails(admin);
    }

    @Test
    @DisplayName("유저네임을 정상적으로 반환")
    void testGetUsername() {
        assertThat(adminDetails.getUsername()).isEqualTo("admin");
    }

    @Test
    @DisplayName("비밀번호를 정상적으로 반환")
    void testGetPassword() {
        assertThat(adminDetails.getPassword()).isEqualTo("password123");
    }

    @Test
    @DisplayName("관리자는 ROLE_ADMIN 권한 가짐.")
    void testGetAuthorities() {
        Collection<? extends GrantedAuthority> authorities = adminDetails.getAuthorities();
        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    @DisplayName("계정이 활성화되어 있어야 함.")
    void testIsEnabled() {
        assertThat(adminDetails.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("로그인 실패 횟수가 5 미만이면 계정이 잠기지 않아야 함.")
    void testIsAccountNonLocked() {
        assertThat(adminDetails.isAccountNonLocked()).isTrue();
    }

    @Test
    @DisplayName("로그인 실패 횟수가 5 이상이면 계정이 잠겨야 함.")
    void testIsAccountLocked() {
        admin.setFailureCount(5);
        assertThat(new AdminDetails(admin).isAccountNonLocked()).isFalse();
    }
}
