package com.pofo.backend.domain.project.repository;

import com.pofo.backend.domain.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

   List<Project>  findAllByOrderByIdDesc();
}
