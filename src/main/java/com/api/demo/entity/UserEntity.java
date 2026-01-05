package com.api.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "SYS_USERS_PORTAL", schema = "IKHEALTH")
@Data
public class UserEntity {

    @Id
    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "USER_NAME")
    private String username;

    @Column(name = "USER_PASSWORD")
    private String password;

    @Column(name = "ACTIVE")
    private Long active;

    @Column(name = "PATIENT_ID")
    private Long patientId;
}
