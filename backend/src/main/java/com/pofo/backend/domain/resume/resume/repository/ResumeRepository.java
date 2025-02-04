package com.pofo.backend.domain.resume.resume.repository;

import com.pofo.backend.domain.resume.resume.entity.Resume;

import java.util.Optional;

import com.pofo.backend.domain.user.join.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    Optional<Resume> findByUser(User user);
}
