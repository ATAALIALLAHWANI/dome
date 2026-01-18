package com.api.demo.dto.interfaceDto;

import java.math.BigDecimal;
import java.sql.Timestamp;

public interface PatientSearchResult {
    // Patient fields - use BigDecimal for Oracle NUMBER type
    BigDecimal getId();
    BigDecimal getPatientNo();
    BigDecimal getSiteId();
    String getFirstNameA();
    String getFirstNameE();
    String getFatherNameA();
    String getFatherNameE();
    String getGrandfatherNameA();
    String getGrandfatherNameE();
    String getLastNameA();
    String getLastNameE();
    String getFullNameArb();
    String getFullNameEng();
    BigDecimal getSex();
    Timestamp getDateOfBirth();  // Changed from java.sql.Date to Timestamp
    String getBirthPlace();
    String getNationalIdNo();
    BigDecimal getNationalValue();
    BigDecimal getFileStatus();
    String getCreatedBy();
    Timestamp getCreationDate();
    String getLastUpdatedBy();
    Timestamp getLastUpdatedDate();
    
    // Patient details fields
    String getMobileNo();
    String getEmail();
    String getAdress();
    BigDecimal getMaritalStatusValue();
}