package com.api.demo.model;

import lombok.Data;

@Data

public class LoginResponseDto {
    private Long userId;
    private PatientDto patient;
    // private String token;   // if using JWT
}
