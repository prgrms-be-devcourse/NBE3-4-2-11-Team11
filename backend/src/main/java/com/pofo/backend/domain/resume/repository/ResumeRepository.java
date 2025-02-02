package com.pofo.backend.domain.resume.repository;

import com.pofo.backend.domain.resume.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Integer> {

}
