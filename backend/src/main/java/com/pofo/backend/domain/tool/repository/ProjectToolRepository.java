package com.pofo.backend.domain.tool.repository;

import com.pofo.backend.domain.tool.entity.ProjectTool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectToolRepository extends JpaRepository<ProjectTool, Long> {
    void deleteByProjectId(Long projectId);
    List<ProjectTool> findByProjectId(Long projectId);

    void deleteByProjectIdIn(List<Long> projectIds);
}

