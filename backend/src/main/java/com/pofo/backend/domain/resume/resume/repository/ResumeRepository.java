package com.pofo.backend.domain.resume.resume.repository;

import com.pofo.backend.domain.resume.resume.entity.Resume;
import com.pofo.backend.domain.user.join.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    Optional<Resume> findByUser(User user);
}
