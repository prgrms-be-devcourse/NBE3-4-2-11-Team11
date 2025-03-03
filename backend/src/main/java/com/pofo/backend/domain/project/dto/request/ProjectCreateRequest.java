package com.pofo.backend.domain.project.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectCreateRequest {

    @NotBlank
    private String name;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    @NotNull
    private int memberCount;
    @NotBlank
    private String position;

    private String repositoryLink;

    @NotBlank
    private String description;
    @NotBlank
    private String imageUrl;

    private String thumbnailPath;

    // 기술 및 도구 목록 추가
    @NotEmpty
    private List<String> skills;
    @NotEmpty
    private List<String> tools;


}
