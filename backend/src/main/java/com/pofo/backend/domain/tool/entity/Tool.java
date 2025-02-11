package com.pofo.backend.domain.tool.entity;

import com.pofo.backend.common.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="tools")
@Getter
public class Tool extends BaseEntity {

    @Column(nullable = false,  unique = true)
    private String name;

    @OneToMany(mappedBy = "tool", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResumeTool> resumeTools = new ArrayList<>();
    @OneToMany(mappedBy = "tool", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectTool> projectTools = new ArrayList<>();

    //테스트 코드에서 사용 가능한 public한 생성자 추가
    public Tool(String name){
        this.name = name;
    }
}