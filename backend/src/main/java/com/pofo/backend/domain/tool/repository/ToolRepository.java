package com.pofo.backend.domain.tool.repository;

import com.pofo.backend.domain.tool.dto.ToolProjection;
import com.pofo.backend.domain.tool.entity.Tool;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ToolRepository extends JpaRepository<Tool, Long> {
    Optional<Tool> findByName(String name);

    @Query("SELECT new com.pofo.backend.domain.tool.dto.ToolProjectionImpl(t.id, t.name) FROM Tool t")
    List<ToolProjection> findAllByProjection();
}
