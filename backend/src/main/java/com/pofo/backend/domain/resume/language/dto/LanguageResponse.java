package com.pofo.backend.domain.resume.language.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LanguageResponse {
    private String language;
    private String name;
    private String result;
    private LocalDate certifiedDate;
}
