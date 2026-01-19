package com.api.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "SYS_USERS", schema = "IKHEALTH")
@Data
public class SysUserEntity {

    @Id
    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "USER_NAME", nullable = false, length = 30)
    private String userName;

    @Column(name = "USER_PASSWORD", nullable = false, length = 20)
    private String userPassword;

    @Column(name = "ACTIVE", length = 5)
    private String active;

    @Column(name = "USER_TYPE")
    private Long userType;

    @Column(name = "PATIENT_ID")
    private Long patientId;

    @Column(name = "STAFF_ID")
    private Long staffId;

    @Column(name = "CREATED_BY", nullable = false, length = 30)
    private String createdBy;

    @Column(name = "CREATION_DATE", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "LAST_UPDATED_BY", length = 30)
    private String lastUpdatedBy;

    @Column(name = "LAST_UPDATED_DATE")
    private LocalDateTime lastUpdatedDate;

    @Column(name = "LAST_UPDATED_TRANSACTION", length = 1)
    private String lastUpdatedTransaction;

    @Column(name = "ADMIN_FLAG")
    private Integer adminFlag;

    @Column(name = "UNREAL_FLAG", length = 3)
    private String unrealFlag;

    @Column(name = "FAV_SCREEN_ID")
    private Long favScreenId;
}
