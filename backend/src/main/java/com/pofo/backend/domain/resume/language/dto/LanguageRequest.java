package com.pofo.backend.domain.resume.language.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LanguageRequest {
    @NotBlank
    private String language;
    @NotBlank
    private String name;
    @NotBlank
    private String result;
    @NotNull
    private LocalDate certifiedDate;

}
