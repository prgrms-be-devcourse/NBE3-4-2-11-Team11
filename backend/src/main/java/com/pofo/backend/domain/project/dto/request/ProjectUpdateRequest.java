package com.pofo.backend.domain.project.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ProjectUpdateRequest {

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
}
