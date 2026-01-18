package com.api.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api.demo.dto.CreatePatientRequest;
import com.api.demo.dto.interfaceDto.PatientSearchResult;
import com.api.demo.entity.PatientEntity;

@Repository
public interface PatientRepository extends JpaRepository<PatientEntity, Long> {

      boolean existsByNationalIdNo(String nationalIdNo);
    
    boolean existsByPatientNo(Long patientNo);
    
    // Find by patient number
    PatientEntity findByPatientNo(Long patientNo);
     // Dynamic search query joining both tables
    @Query(value = 
        "SELECT * FROM ( " +
        "  SELECT inner_query.*, ROWNUM rnum FROM ( " +
        "    SELECT " +
        "      p.ID, p.PATIENT_NO, p.SITE_ID, " +
        "      p.FIRST_NAME_A, p.FIRST_NAME_E, " +
        "      p.FATHER_NAME_A, p.FATHER_NAME_E, " +
        "      p.GRANDFATHER_NAME_A, p.GRANDFATHER_NAME_E, " +
        "      p.LAST_NAME_A, p.LAST_NAME_E, " +
        "      p.FULL_NAME_ARB, p.FULL_NAME_ENG, " +
        "      p.SEX, p.DATE_OF_BIRTH, p.BIRTH_PLACE, " +
        "      p.NATIONAL_ID_NO, p.NATIONAL_VALUE, p.FILE_STATUS, " +
        "      p.CREATED_BY, p.CREATION_DATE, " +
        "      p.LAST_UPDATED_BY, p.LAST_UPDATED_DATE, " +
        "      d.MOBILE_NO, d.E_MAIL, d.ADRESS, d.MARITAL_STATUS_VALUE " +
        "    FROM IKHEALTH.SYS_PATIENTS p " +
        "    LEFT JOIN IKHEALTH.SYS_PATIENT_DETAILS d ON p.ID = d.PATIENT_ID " +
        "    WHERE p.SITE_ID = :siteId " +
        "    AND REGEXP_LIKE( " +
        "      CASE WHEN :isEnglish = 1 THEN UPPER(p.FULL_NAME_ENG) ELSE p.FULL_NAME_ARB END, " +
        "      :regexPattern, 'i' " +
        "    ) " +
        "    ORDER BY p.ID " +
        "  ) inner_query WHERE ROWNUM <= :limit " +
        ") WHERE rnum > 0",
        nativeQuery = true)
    List<PatientSearchResult> searchPatientsByNameDynamic(
        @Param("siteId") Long siteId,
        @Param("isEnglish") Integer isEnglish, // 1 for English, 0 for Arabic
        @Param("regexPattern") String regexPattern,
        @Param("limit") Integer limit
    );
}
