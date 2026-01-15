package com.api.demo.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.Types;

@Service
@AllArgsConstructor
public class PatientCheckService {

    private final JdbcTemplate jdbcTemplate;

  public String checkPatientDuplicate(
        String firstName,
        String fatherName,
        String grandfatherName,
        String lastName,
        String phone,
        java.sql.Date dateOfBirth) {

    String sql = "{CALL IKHEALTH.CHECK_PATIENT_DUPLICATE(?,?,?,?,?,?,?)}";

    return jdbcTemplate.execute(sql, (CallableStatement cs) -> {
        cs.setString(1, phone);
        cs.setString(2, firstName);
        cs.setString(3, fatherName);
        cs.setString(4, grandfatherName);
        cs.setString(5, lastName);
        cs.setDate(6, dateOfBirth); // <-- MUST be java.sql.Date
        cs.registerOutParameter(7, Types.VARCHAR);

        cs.execute();
        return cs.getString(7);
    });
}

   
}
