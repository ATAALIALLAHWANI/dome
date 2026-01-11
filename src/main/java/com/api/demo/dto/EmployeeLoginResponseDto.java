package com.api.demo.dto;

import lombok.Data;

@Data
public class EmployeeLoginResponseDto {

    private Long userId;
    private Long userType;
    private String empNameEng;
    private String empNameArb;
    private Long siteId;
    private Long deptId;
    private Long empType;
    private String siteDescArb;
    private String siteDescEng;
    private Long staffId;
}
