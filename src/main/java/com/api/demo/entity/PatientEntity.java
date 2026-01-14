package com.api.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "SYS_PATIENTS", schema = "IKHEALTH")
@Data
public class PatientEntity {

    /* ===================== PK ===================== */

   @Id
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SYS_PATIENTS_SEQ")
@SequenceGenerator(
    name = "SYS_PATIENTS_SEQ",
    sequenceName = "SYS_PATIENTS_SEQ",
    allocationSize = 1
)
@Column(name = "ID")
    private Long id;

    /* ===================== CORE ===================== */

    @Column(name = "SITE_ID", nullable = false)
    private Long siteId = 1L;

    @Column(name = "PATIENT_NO", nullable = false, length = 13)
    private Long patientNo;

    @Column(name = "NATIONAL_VALUE")
    private Long nationalValue;

    @Column(name = "SEX")
    private Long sex; // 1=Male, 2=Female

    @Column(name = "DATE_OF_BIRTH")
    private LocalDate dateOfBirth;

    @Column(name = "BIRTH_PLACE", length = 60)
    private String birthPlace;

    /* ===================== ARABIC NAMES ===================== */

    @Column(name = "FIRST_NAME_A", length = 4000)
    private String firstNameA;

    @Column(name = "FATHER_NAME_A", length = 4000)
    private String fatherNameA;

    @Column(name = "GRANDFATHER_NAME_A", length = 4000)
    private String grandfatherNameA;

    @Column(name = "LAST_NAME_A", length = 4000)
    private String lastNameA;

    @Column(name = "MOTHER_NAME_A", length = 4000)
    private String motherNameA;

    /* ===================== ENGLISH NAMES ===================== */

    @Column(name = "FIRST_NAME_E", length = 4000)
    private String firstNameE;

    @Column(name = "FATHER_NAME_E", length = 4000)
    private String fatherNameE;

    @Column(name = "GRANDFATHER_NAME_E", length = 4000)
    private String grandfatherNameE;

    @Column(name = "LAST_NAME_E", length = 4000)
    private String lastNameE;

    @Column(name = "MOTHER_NAME_E", length = 4000)
    private String motherNameE;

    /* ===================== IDENTIFICATION ===================== */

    @Column(name = "NATIONAL_ID_NO", length = 15)
    private String nationalIdNo;

    @Column(name = "PATIENT_TITLE_VALUE")
    private Long patientTitleValue;

    @Column(name = "BLD_GROUP", length = 2)
    private String bloodGroup;

    @Column(name = "RHESUS_FACTOR", length = 1)
    private String rhesusFactor;

    @Column(name = "MOTHER_NATIONAL_ID", length = 15)
    private String motherNationalId;

    @Column(name = "FATHER_NATIONAL_ID", length = 15)
    private String fatherNationalId;

    /* ===================== BLOBS ===================== */

    @Lob
    @Column(name = "RESIDENCY_CARD_IMG")
    private byte[] residencyCardImg;

    @Lob
    @Column(name = "PATIENT_PHOTO")
    private byte[] patientPhoto;

    /* ===================== AUDIT ===================== */

    @Column(name = "CREATED_BY", nullable = false, updatable = false)
    private String createdBy;

    @Column(name = "CREATION_DATE", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "LAST_UPDATED_BY")
    private String lastUpdatedBy;

    @Column(name = "LAST_UPDATED_DATE")
    private LocalDateTime lastUpdatedDate;

    @Column(name = "LAST_UPDATED_TRANSACTION", length = 1)
    private String lastUpdatedTransaction;

    /* ===================== ACCOUNT / FILTER ===================== */

    @Column(name = "ACC_CODE", length = 15)
    private String accCode;

    @Column(name = "FLTR_FULL_NAME_ARB", length = 4000)
    private String filterFullNameAr;

    @Column(name = "FULL_NAME_ARB", length = 4000)
    private String fullNameAr;

    @Column(name = "FULL_NAME_ENG", length = 4000)
    private String fullNameEng;

    @Column(name = "FLTR_FULL_NAME_ENG", length = 4000)
    private String filterFullNameEng;

    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    /* ===================== ALIAS ===================== */

    @Column(name = "ALIAS_NAME_1", length = 1000)
    private String aliasName1;

    @Column(name = "ALIAS_NAME_2", length = 1000)
    private String aliasName2;

    @Column(name = "ALIAS_FLAG")
    private Long aliasFlag = 2L;

    /* ===================== STATUS ===================== */

    @Column(name = "FILE_STATUS")
    private Long fileStatus;

    @Column(name = "INACTIVE_DATE")
    private LocalDate inactiveDate;

    /* ===================== LIFECYCLE ===================== */

    @PrePersist
    public void prePersist() {
        this.createdBy = "api-siq";
        this.creationDate = LocalDateTime.now();
        this.lastUpdatedBy = "api-siq";
        this.lastUpdatedDate = LocalDateTime.now();
        this.lastUpdatedTransaction = "I";
    }

    @PreUpdate
    public void preUpdate() {
        this.lastUpdatedBy = "api-siq";
        this.lastUpdatedDate = LocalDateTime.now();
        this.lastUpdatedTransaction = "U";
    }
}
