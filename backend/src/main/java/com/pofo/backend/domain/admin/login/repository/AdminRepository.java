package com.pofo.backend.domain.admin.login.repository;

import com.pofo.backend.domain.admin.login.entitiy.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByUsername(String username);  // username으로 관리자 찾기
}
