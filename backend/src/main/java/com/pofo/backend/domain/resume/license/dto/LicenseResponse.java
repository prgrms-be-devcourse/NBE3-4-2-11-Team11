package com.pofo.backend.domain.resume.license.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LicenseResponse {
    private String name;
    private String institution;
    private LocalDate certifiedDate;
}