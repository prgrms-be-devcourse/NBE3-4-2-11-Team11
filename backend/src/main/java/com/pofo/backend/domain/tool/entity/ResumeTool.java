package com.pofo.backend.domain.tool.entity;

import com.pofo.backend.common.jpa.entity.BaseEntity;
import com.pofo.backend.domain.resume.resume.entity.Resume;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="resume_tools")
@Getter
public class ResumeTool extends BaseEntity {

    @ManyToOne
    @JoinColumn(name="resume_id", nullable = false)
    private Resume resume;

    @ManyToOne
    @JoinColumn(name="tool_id", nullable = false)
    private Tool tool;

}