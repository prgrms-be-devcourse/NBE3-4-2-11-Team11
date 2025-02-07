package com.pofo.backend.domain.resume.resume.repository;

import com.pofo.backend.domain.resume.resume.entity.Resume;
import com.pofo.backend.domain.user.join.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    @Query("SELECT r FROM Resume r " +
        "LEFT JOIN FETCH r.activities " +
        "LEFT JOIN FETCH r.courses " +
        "LEFT JOIN FETCH r.experiences " +
        "LEFT JOIN FETCH r.educations " +
        "LEFT JOIN FETCH r.licenses " +
        "LEFT JOIN FETCH r.languages " +
        "WHERE r.user = :user")
    Optional<Resume> findResumeWithDetails(@Param("user") User user);

    Optional<Resume> findByUser(User user);
}
