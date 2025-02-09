package com.pofo.backend.domain.tool.repository;

import com.pofo.backend.domain.tool.entity.Tool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ToolRepository extends JpaRepository<Tool, Long> {
    Optional<Tool> findByName(String name);
}
