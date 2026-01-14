package com.api.demo.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // IMPORTANT: This allows partial JSON
public class UpdatePatientRequest {
    private String firstNameAr;
    private String firstNameEn;
    private String fatherNameAr;
    private String fatherNameEn;
    private String grandfatherNameAr;
    private String grandfatherNameEn;
    private String lastNameAr;
    private String lastNameEn;
    
    private Long gender;
    private Long maritalStatus;
    private Long nationality;
    private LocalDate dateOfBirth;
    
    private String mobile;
    private String email;
    private String address;
    private String placeOfBirth;
    private String nationalNo;
}