package com.api.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "SYS_PATIENTS", schema = "IKHEALTH")
@Data
public class PatientEntity {

     @Id
    @Column(name = "ID") 
    private Long patientId;

    @Column(name = "FULL_NAME_ARB")
    private String fullNameAr;

    @Column(name = "FULL_NAME_ENG")
    private String fullNameEn;

    @Column(name = "DATE_OF_BIRTH")
    private LocalDate dateOfBirth;

    @Column(name = "NATIONAL_ID_NO")
    private String nationalId;
}
