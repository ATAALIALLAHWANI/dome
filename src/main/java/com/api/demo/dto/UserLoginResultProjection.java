package com.api.demo.dto;

public interface UserLoginResultProjection {

    Long getUserId();
    String getUserName();
    String getActive();
    Long getUserType();
    Long getPatientId();
    Long getStaffId();
    Integer getAdminFlag();

    Long getSiteId();
    Long getDeptId();
    String getEmpNameArb();
    String getEmpNameEng();
    Long getEmpType();
    String getEmpDiscipline();

    String getSiteDescArb();
    String getSiteDescEng();

    Long getSitePrivilageId();
}
