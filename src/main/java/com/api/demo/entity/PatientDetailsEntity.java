package com.api.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "SYS_PATIENT_DETAILS", schema = "IKHEALTH")
@Data
public class PatientDetailsEntity {

    @Id
    @Column(name = "PATIENT_ID")
    private Long patientId;

    @Column(name = "MOBILE_NO")
    private String mobile;

    @Column(name = "E_MAIL")
    private String email;
}
