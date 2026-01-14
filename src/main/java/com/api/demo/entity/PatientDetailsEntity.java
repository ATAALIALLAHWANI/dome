package com.api.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "SYS_PATIENT_DETAILS", schema = "IKHEALTH")
@Data
public class PatientDetailsEntity {

    /* ===================== PK / FK ===================== */

    @Id
    @Column(name = "PATIENT_ID", nullable = false)
    private Long patientId;

    /* ===================== PERSONAL ===================== */

    @Column(name = "MARITAL_STATUS_VALUE")
    private Long maritalStatusValue;

    @Column(name = "PATIENT_TYPE")
    private Long patientType;

    @Column(name = "PATIENT_CLASS_VALUE")
    private Long patientClassValue;

    @Column(name = "EMPLOYEE_RANK_VALUE")
    private Long employeeRankValue;

    @Column(name = "OCCUPATION_VALUE")
    private Long occupationValue;

    @Column(name = "EDUCATION_LEVEL_VALUE")
    private Long educationLevelValue;

    @Column(name = "PATIENT_LANG_VALUE")
    private Long patientLangValue;

    @Column(name = "COMPLETE_FLAG")
    private Long completeFlag;

    /* ===================== CONTACT ===================== */

    @Column(name = "MOBILE_NO", length = 15)
    private String mobileNo;

    @Column(name = "HOME_PHONE", length = 15)
    private String homePhone;

    @Column(name = "WORK_PHONE", length = 15)
    private String workPhone;

    @Column(name = "E_MAIL", length = 40)
    private String email;

    /* ===================== ADDRESS ===================== */

    @Column(name = "ADRESS", length = 256)
    private String address;

    @Column(name = "ADDRESS2", length = 300)
    private String address2;

    @Column(name = "POST_CODE", length = 6)
    private String postCode;

    @Column(name = "P_O_BOX", length = 20)
    private String poBox;

    /* ===================== NOTES ===================== */

    @Column(name = "PATIENT_NOTES", length = 1000)
    private String patientNotes;

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
