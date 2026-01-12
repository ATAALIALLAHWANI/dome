package com.api.demo.dto;

import lombok.Data;

@Data
public class EmployeeLoginWithTokenDto {
    private String token;
    private EmployeeLoginResponseDto employee;
}
