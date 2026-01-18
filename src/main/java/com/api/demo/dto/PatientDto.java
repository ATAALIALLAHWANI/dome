package com.api.demo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PatientDto {
    private Long patientId;
    private Long patientNo;
    private Long siteId ;
    private String firstNameAr;
    private String firstNameEn;
    private String fatherNameAr;
    private String fatherNameEn;
    private String grandfatherNameAr;
    private String grandfatherNameEn;
    private String lastNameAr;
    private String lastNameEn;
    private String fullNameAr;
    private String fullNameEn;
    
    private Long gender;
    private Long maritalStatus;
    private Long nationality;
    private LocalDate dateOfBirth;
    
    private String mobile;
    private String email;
    private String address;
    private String placeOfBirth;
    private String nationalNo;
    
    private String createdBy;
    private LocalDateTime creationDate;
    private String lastUpdatedBy;
    private LocalDateTime lastUpdatedDate;
}