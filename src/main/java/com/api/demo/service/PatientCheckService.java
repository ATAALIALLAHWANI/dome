package com.api.demo.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import java.sql.CallableStatement;
import java.sql.Timestamp;
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
            Timestamp dateOfBirth) {

        String sql = "{CALL CHECK_PATIENT_DUPLICATE(?,?,?,?,?,?,?)}";

        return jdbcTemplate.execute(sql, (CallableStatement cs) -> {
            cs.setString(1, phone);
            cs.setString(2, firstName);
            cs.setString(3, fatherName);
            cs.setString(4, grandfatherName);
            cs.setString(5, lastName);
            cs.setTimestamp(6, dateOfBirth);
            cs.registerOutParameter(7, Types.VARCHAR);

            cs.execute();
            return cs.getString(7);
        });
    }


     /**
     * Check for duplicate patient excluding current patient (for updates)
     */
    public String checkPatientDuplicateForUpdate(
            Long excludePatientId,
            String firstName,
            String fatherName,
            String grandfatherName,
            String lastName,
            String phone,
            Timestamp dateOfBirth) {

        String sql = "{CALL CHECK_PATIENT_DUPLICATE_EXCLUDE(?,?,?,?,?,?,?,?)}";

        return jdbcTemplate.execute(sql, (CallableStatement cs) -> {
            cs.setLong(1, excludePatientId);
            cs.setString(2, phone);
            cs.setString(3, firstName);
            cs.setString(4, fatherName);
            cs.setString(5, grandfatherName);
            cs.setString(6, lastName);
            cs.setTimestamp(7, dateOfBirth);
            cs.registerOutParameter(8, Types.VARCHAR);

            cs.execute();
            return cs.getString(8);
        });
    }
}

