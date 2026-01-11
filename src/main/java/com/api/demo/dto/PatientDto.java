package com.api.demo.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PatientDto {
    private Long patientId;
    private String fullNameAr;
    private String fullNameEn;
    private LocalDate dateOfBirth;
    private String nationalId;
    private String mobile;
    private String email;
}
