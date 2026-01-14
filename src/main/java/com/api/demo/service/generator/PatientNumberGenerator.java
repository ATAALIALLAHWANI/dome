package com.api.demo.service.generator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.api.demo.dao.HISDmlStatement;
import com.api.demo.util.DbUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PatientNumberGenerator {

    @Autowired
    private DataSource dataSource;
    
    // Synchronized lock for thread safety
    private static final Object LOCK = new Object();

    /**
     * Generate next patient number using MAX+1
     */
    public Long generateNextPatientNumber() throws SQLException {
        synchronized (LOCK) {
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            
            try {
                conn = dataSource.getConnection();
                pstmt = conn.prepareStatement(HISDmlStatement.GET_NEXT_PATIENT_NO);
                rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    Long patientNo = rs.getLong(1);
                    log.debug("Generated patient number: {}", patientNo);
                    return patientNo;
                }
                
                return 1L; // First patient
                
            } finally {
                DbUtils.closeAll(rs, pstmt, conn);
            }
        }
    }

    /**
     * Generate next patient number with validation
     */
    public Long generateNextPatientNumberWithValidation() throws SQLException {
        Long patientNo = generateNextPatientNumber();
        
        // Validate that the number doesn't exist (rare case)
        if (isPatientNumberExists(patientNo)) {
            log.warn("Patient number {} already exists, generating next", patientNo);
            return generateNextPatientNumber() + 1;
        }
        
        return patientNo;
    }

    /**
     * Check if patient number exists
     */
    private boolean isPatientNumberExists(Long patientNo) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(HISDmlStatement.CHECK_PATIENT_NO);
            pstmt.setLong(1, patientNo);
            rs = pstmt.executeQuery();
            
            return rs.next() && rs.getInt(1) > 0;
        } finally {
            DbUtils.closeAll(rs, pstmt, conn);
        }
    }
}