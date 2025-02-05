package com.pofo.backend.domain.resume.license.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LicenseResponse {
    private String name;
    private String institution;
    private LocalDate certifiedDate;
}