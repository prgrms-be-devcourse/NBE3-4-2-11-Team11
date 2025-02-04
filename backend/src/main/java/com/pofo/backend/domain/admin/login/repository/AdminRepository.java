package com.pofo.backend.domain.admin.login.repository;

import com.pofo.backend.domain.admin.login.entitiy.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUsername(String username);  // username으로 관리자 찾기
}
