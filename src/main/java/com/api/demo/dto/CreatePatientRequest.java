package com.api.demo.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreatePatientRequest extends BasePatientRequest {

    @NotBlank
    @Override
    public String getFirstNameAr() { return super.getFirstNameAr(); }

    @NotBlank
    @Override
    public String getFirstNameEn() { return super.getFirstNameEn(); }

    @NotBlank
    @Override
    public String getFatherNameAr() { return super.getFatherNameAr(); }

    @NotBlank
    @Override
    public String getFatherNameEn() { return super.getFatherNameEn(); }

    @NotBlank
    @Override
    public String getLastNameAr() { return super.getLastNameAr(); }

    @NotBlank
    @Override
    public String getLastNameEn() { return super.getLastNameEn(); }

    @NotNull
    @Override
    public Long getGender() { return super.getGender(); }

    @NotNull
    @Override
    public Long getMaritalStatus() { return super.getMaritalStatus(); }

    @NotNull
    @Override
    public Long getNationality() { return super.getNationality(); }

    @NotNull
    @Override
    public LocalDate getDateOfBirth() { return super.getDateOfBirth(); }
}
