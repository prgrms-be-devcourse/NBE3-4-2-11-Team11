package com.pofo.backend.domain.resume.activity.award.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AwardRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String institution;
    @NotNull
    private LocalDate awardDate;

}