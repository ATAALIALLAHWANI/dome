package com.api.demo.dto;

import lombok.Data;

@Data
public class UserLoginResultDto {

    // ===== SYS_USERS =====
    private Long userId;
    private String userName;
    private String active;
    private Integer userType;
    private Long patientId;
    private Long staffId;
    private Integer adminFlag;

    // ===== SYS_SITE_EMPLOYEES =====
    private Long siteId;
    private Long deptId;
    private String empNameArb;
    private String empNameEng;
    private String empType;
    private String empDiscipline;

    // ===== SYS_SITES =====
    private String siteDescArb;
    private String siteDescEng;

    // ===== SUB QUERY =====
    private Long sitePrivilageId;
}
