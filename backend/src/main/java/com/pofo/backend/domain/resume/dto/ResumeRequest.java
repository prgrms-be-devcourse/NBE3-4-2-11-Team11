package com.pofo.backend.domain.resume.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Date;

public class ResumeRequest {

    @NotBlank
    private String name;
    @NotBlank
    private Date birth;
    @NotBlank
    private String number;
    @NotBlank
    private String email;
    @NotBlank
    private String address;
    private String gitAddress;
    private String blogAddress;

}
