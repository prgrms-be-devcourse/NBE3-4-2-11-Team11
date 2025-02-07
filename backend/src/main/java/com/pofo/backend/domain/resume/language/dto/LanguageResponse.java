package com.pofo.backend.domain.resume.language.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LanguageResponse {
    private String language;
    private String name;
    private String result;
    private LocalDate certifiedDate;
}
