package com.pofo.backend.domain.admin.userstats.repository;


import com.pofo.backend.domain.user.join.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

@Repository
public interface UserStatsRepository extends JpaRepository<User, Long> {

    // 가입일 범위로 조회하는 메소드 예시
    Page<User> findAllByCreatedAtBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    // 성별로 조회하는 메소드 예시
    Page<User> findAllBySex(User.Sex sex, Pageable pageable);

    // 성별과 가입일 범위를 함께 조회하는 메소드 예시
    Page<User> findAllBySexAndCreatedAtBetween(User.Sex sex, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
