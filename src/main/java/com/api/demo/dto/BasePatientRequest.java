package com.api.demo.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class BasePatientRequest {

    protected String firstNameAr;
    protected String firstNameEn;
    protected String fatherNameAr;
    protected String fatherNameEn;
    protected String grandfatherNameAr;
    protected String grandfatherNameEn;
    protected String lastNameAr;
    protected String lastNameEn;

    protected Long gender;
    protected Long maritalStatus;
    protected Long nationality;
    protected LocalDate dateOfBirth;
    protected Long flagStatus;

    protected String mobile;
    protected String email;
    protected String address;
    protected String placeOfBirth;
    protected String nationalNo;
}
