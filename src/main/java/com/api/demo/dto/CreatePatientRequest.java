package com.api.demo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreatePatientRequest {

    /* ===== Mandatory (*) ===== */

    @NotBlank
    private String firstNameAr;

    @NotBlank
    private String firstNameEn;

    @NotBlank
    private String fatherNameAr;

    @NotBlank
    private String fatherNameEn;

    @NotBlank
    private String lastNameAr;

    @NotBlank
    private String lastNameEn;

    @NotNull
    private Long gender; // 1=Male, 2=Female

    @NotNull
    private Long maritalStatus;

    @NotNull
    private Long nationality;

    @NotNull
    private LocalDate dateOfBirth;

    /* ===== Optional ===== */

    private String grandfatherNameAr;
    private String grandfatherNameEn;
    private String mobile;
    private String email;
    private String address;
    private String placeOfBirth;
    private String nationalNo;
}
