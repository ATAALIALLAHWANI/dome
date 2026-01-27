package com.api.demo.service;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import com.api.demo.dao.HISDmlStatement;
import com.api.demo.dto.CreatePatientRequest;
import com.api.demo.dto.PatientDto;
import com.api.demo.dto.UpdatePatientRequest;
import com.api.demo.dto.interfaceDto.PatientSearchResult;
import com.api.demo.entity.PatientDetailsEntity;
import com.api.demo.entity.PatientEntity;
import com.api.demo.repository.PatientDetailsRepository;
import com.api.demo.repository.PatientRepository;
import com.api.demo.security.EmployeePrincipal;
import com.api.demo.service.generator.PatientNumberGenerator;
import com.api.demo.util.DbUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation; // Optional, for advanced transaction 
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PatientService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PatientNumberGenerator patientNumberGenerator;

    @Autowired
    private PatientCheckService patientCheckService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientDetailsRepository patientDetailsRepository;

    public Long createPatient(CreatePatientRequest req) {
        log.info("Creating patient: {} {}", req.getFirstNameEn(), req.getLastNameEn());

        /* ================= VALIDATE REQUIRED FIELDS ================= */
        validateRequiredFields(req);

        Connection conn = null;
        PreparedStatement pstmtPatient = null;
        PreparedStatement pstmtDetails = null;
        ResultSet rs = null;

        try {
            // Get connection and start transaction
            conn = dataSource.getConnection();
            DbUtils.setAutoCommit(conn, false);

            //////////

            /* ================= DUPLICATE CHECK (INSIDE TRANSACTION) ================= */
            // Call the duplicate check service
            String duplicateResult = patientCheckService.checkPatientDuplicate(
                    req.getFirstNameAr(),
                    req.getFatherNameAr(),
                    req.getGrandfatherNameAr(),
                    req.getLastNameAr(),
                    req.getMobile(),
                    java.sql.Date.valueOf(req.getDateOfBirth()));
            // If duplicate exists, throw HTTP 409 Conflict with exact message from SP
            if (duplicateResult != null && !"Y".equalsIgnoreCase(duplicateResult.trim())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, duplicateResult);
            }

            /* ================= GENERATE PATIENT NUMBER ================= */
            Long patientNo = patientNumberGenerator.generateNextPatientNumberWithValidation();
            log.info("Generated patient number: {}", patientNo);

            /* ================= VALIDATE NATIONAL ID ================= */
            if (req.getNationalNo() != null && !req.getNationalNo().trim().isEmpty()) {
                validateNationalId(req.getNationalNo(), conn);
            }

            /* ================= GENERATE FULL NAMES ================= */
            PatientName patientName = generatePatientNames(req);

            /* ================= INSERT PATIENT ================= */
            Long patientId = insertPatient(conn, req, patientNo, patientName);

            /* ================= INSERT PATIENT DETAILS ================= */
            insertPatientDetails(conn, patientId, req);

            // Commit transaction
            conn.commit();
            log.info("Patient created successfully - ID: {}, Number: {}", patientId, patientNo);

            return patientNo;

        } catch (SQLException e) {
            log.error("Database error creating patient: {}", e.getMessage(), e);
            DbUtils.rollback(conn);

            if (e.getMessage() != null && e.getMessage().contains("ORA-00001")) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Patient already exists or duplicate data");
            }

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error creating patient: " + e.getMessage());

        } catch (ResponseStatusException e) {
            DbUtils.rollback(conn);
            throw e;

        } catch (Exception e) {
            log.error("Unexpected error creating patient: {}", e.getMessage(), e);
            DbUtils.rollback(conn);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unexpected error creating patient: " + e.getMessage());

        } finally {
            DbUtils.closeAll(rs, pstmtPatient, pstmtDetails, conn);
        }
    }

    /**
     * Validate all required fields
     */
    private void validateRequiredFields(CreatePatientRequest req) {
        StringBuilder errors = new StringBuilder();

        // *First Name (Arb) : Required
        if (req.getFirstNameAr() == null || req.getFirstNameAr().trim().isEmpty()) {
            errors.append("First Name (Arabic) is required. ");
        }

        // *First Name (Eng) : Required
        if (req.getFirstNameEn() == null || req.getFirstNameEn().trim().isEmpty()) {
            errors.append("First Name (English) is required. ");
        }

        // *Father Name (Arb) : Required
        if (req.getFatherNameAr() == null || req.getFatherNameAr().trim().isEmpty()) {
            errors.append("Father Name (Arabic) is required. ");
        }

        // *Father Name (Eng) : Required
        if (req.getFatherNameEn() == null || req.getFatherNameEn().trim().isEmpty()) {
            errors.append("Father Name (English) is required. ");
        }

        // *Last Name (Arb) : Required
        if (req.getLastNameAr() == null || req.getLastNameAr().trim().isEmpty()) {
            errors.append("Last Name (Arabic) is required. ");
        }

        // *Last Name (Eng) : Required
        if (req.getLastNameEn() == null || req.getLastNameEn().trim().isEmpty()) {
            errors.append("Last Name (English) is required. ");
        }

        // Date of Birth : Required
        if (req.getDateOfBirth() == null) {
            errors.append("Date of Birth is required. ");
        } else {
            // Validate date is not in the future
            if (req.getDateOfBirth().isAfter(LocalDate.now())) {
                errors.append("Date of Birth cannot be in the future. ");
            }
            // Validate reasonable age (e.g., not older than 150 years)
            if (req.getDateOfBirth().isBefore(LocalDate.now().minusYears(150))) {
                errors.append("Date of Birth appears to be invalid. ");
            }
        }

        // Nationality : Required
        if (req.getNationality() == null) {
            errors.append("Nationality is required. ");
        }

        // *Mobile Phone : Required
        if (req.getMobile() == null || req.getMobile().trim().isEmpty()) {
            errors.append("Mobile Phone is required. ");
        } else {
            // Validate mobile number (more than 3 digits)
            String mobile = req.getMobile().trim();
            if (!mobile.matches("^[0-9]{4,}$")) {
                errors.append("Mobile Phone must contain more than 3 digits. ");
            }

        }

        // Gender : Required
        if (req.getGender() == null) {
            errors.append("Gender is required. ");
        } else if (req.getGender() != 1 && req.getGender() != 2) {
            errors.append("Gender must be 1 (Male) or 2 (Female). ");
        }

        // Marital Status : Required
        if (req.getMaritalStatus() == null) {
            errors.append("Marital Status is required. ");
        }

        // Place of Birth : Required
        if (req.getPlaceOfBirth() == null || req.getPlaceOfBirth().trim().isEmpty()) {
            // errors.append("Place of Birth is required. ");
        }

        // Address : Required
        if (req.getAddress() == null || req.getAddress().trim().isEmpty()) {
            // errors.append("Address is required. ");
        }

        // Email : Optional but validate format if provided
        if (req.getEmail() != null && !req.getEmail().trim().isEmpty()) {
            String email = req.getEmail().trim();
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                errors.append("Email format is invalid. ");
            }
        }

        // Grandfather names: Optional but validate if provided
        if (req.getGrandfatherNameAr() != null && req.getGrandfatherNameAr().trim().isEmpty()) {
            req.setGrandfatherNameAr(null);
        }
        if (req.getGrandfatherNameEn() != null && req.getGrandfatherNameEn().trim().isEmpty()) {
            req.setGrandfatherNameEn(null);
        }

        // National ID: Optional but validate format if provided
        if (req.getNationalNo() != null && !req.getNationalNo().trim().isEmpty()) {
            String nationalNo = req.getNationalNo().trim();
            if (!nationalNo.matches("^[0-9]{10,15}$")) {
                errors.append("National ID must be 10-15 digits. ");
            }
        }

        // If there are validation errors, throw exception
        if (errors.length() > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Validation failed: " + errors.toString().trim());
        }
    }

    /**
     * Insert patient into SYS_PATIENTS
     */
    private Long insertPatient(Connection conn, CreatePatientRequest req,
            Long patientNo, PatientName patientName) throws SQLException {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long siteId = 1L; // Default site ID;
            if (auth != null && auth.getPrincipal() instanceof EmployeePrincipal) {
                EmployeePrincipal principal = (EmployeePrincipal) auth.getPrincipal();
                siteId = principal.getSiteId(); // <-- this is the employee's site
                System.out.println("Site ID for new patient: " + siteId);
            } else {
                // Fallback if user is not authenticated
                System.out.println("No authenticated employee found or principal is not EmployeePrincipal");
            }

            pstmt = conn.prepareStatement(HISDmlStatement.INSERT_SYS_PATIENTS,
                    new String[] { "ID" });

            int paramIndex = 1;
            pstmt.setLong(paramIndex++, siteId); // SITE_ID
            pstmt.setLong(paramIndex++, patientNo); // PATIENT_NO
            pstmt.setObject(paramIndex++, req.getNationality()); // NATIONAL_VALUE
            pstmt.setObject(paramIndex++, req.getGender()); // SEX
            pstmt.setTimestamp(paramIndex++, Timestamp.valueOf(req.getDateOfBirth().atStartOfDay())); // DATE_OF_BIRTH
            pstmt.setString(paramIndex++, req.getPlaceOfBirth()); // BIRTH_PLACE

            // Arabic names
            pstmt.setString(paramIndex++, req.getFirstNameAr());
            pstmt.setString(paramIndex++, req.getFatherNameAr());
            pstmt.setString(paramIndex++, req.getGrandfatherNameAr() != null ? req.getGrandfatherNameAr() : "");
            pstmt.setString(paramIndex++, req.getLastNameAr());

            // English names
            pstmt.setString(paramIndex++, req.getFirstNameEn());
            pstmt.setString(paramIndex++, req.getFatherNameEn());
            pstmt.setString(paramIndex++, req.getGrandfatherNameEn() != null ? req.getGrandfatherNameEn() : "");
            pstmt.setString(paramIndex++, req.getLastNameEn());

            // National ID and full names
            pstmt.setString(paramIndex++, req.getNationalNo() != null ? req.getNationalNo() : "");
            pstmt.setString(paramIndex++, patientName.getFullNameAr());
            pstmt.setString(paramIndex++, patientName.getFullNameEn());
            pstmt.setString(paramIndex++, patientName.getFilterNameAr());
            pstmt.setString(paramIndex++, patientName.getFilterNameEn());

            // Audit fields
            pstmt.setString(paramIndex++, "api-siq");
            pstmt.setString(paramIndex++, "api-siq");
            pstmt.setString(paramIndex++, "I");

            // Flags
            pstmt.setLong(paramIndex++, 2L); // ALIAS_FLAG
            // Flags

            pstmt.setLong(paramIndex++, 2L); // FILE_STATUS

            // Execute insert
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected != 1) {
                throw new SQLException("Failed to insert patient record");
            }

            // Get generated ID
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            }

            throw new SQLException("Failed to get patient ID");

        } finally {
            DbUtils.closeAll(rs, pstmt, null);
        }
    }

    /**
     * Insert patient details into SYS_PATIENT_DETAILS
     */
    private void insertPatientDetails(Connection conn, Long patientId,
            CreatePatientRequest req) throws SQLException {

        PreparedStatement pstmt = null;

        try {
            pstmt = conn.prepareStatement(HISDmlStatement.INSERT_SYS_PATIENT_DETAILS);

            int paramIndex = 1;
            pstmt.setLong(paramIndex++, patientId);
            pstmt.setObject(paramIndex++, req.getMaritalStatus());

            // Other optional fields set to null
            pstmt.setNull(paramIndex++, Types.BIGINT); // PATIENT_TYPE
            pstmt.setNull(paramIndex++, Types.BIGINT); // PATIENT_CLASS_VALUE
            pstmt.setNull(paramIndex++, Types.BIGINT); // EMPLOYEE_RANK_VALUE
            pstmt.setNull(paramIndex++, Types.BIGINT); // OCCUPATION_VALUE
            pstmt.setNull(paramIndex++, Types.BIGINT); // EDUCATION_LEVEL_VALUE
            pstmt.setNull(paramIndex++, Types.BIGINT); // PATIENT_LANG_VALUE
            pstmt.setNull(paramIndex++, Types.BIGINT); // COMPLETE_FLAG

            // Contact info
            pstmt.setString(paramIndex++, req.getMobile());
            pstmt.setNull(paramIndex++, Types.VARCHAR); // HOME_PHONE
            pstmt.setNull(paramIndex++, Types.VARCHAR); // WORK_PHONE
            pstmt.setString(paramIndex++, req.getEmail() != null ? req.getEmail() : ""); // E_MAIL
            pstmt.setString(paramIndex++, req.getAddress());

            // Address fields
            pstmt.setNull(paramIndex++, Types.VARCHAR); // ADDRESS2
            pstmt.setNull(paramIndex++, Types.VARCHAR); // POST_CODE
            pstmt.setNull(paramIndex++, Types.VARCHAR); // P_O_BOX
            pstmt.setNull(paramIndex++, Types.VARCHAR); // PATIENT_NOTES

            // Audit fields
            pstmt.setString(paramIndex++, "api-siq");
            pstmt.setString(paramIndex++, "api-siq");
            pstmt.setString(paramIndex++, "I");

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected != 1) {
                throw new SQLException("Failed to insert patient details");
            }

        } finally {
            DbUtils.close(pstmt);
        }
    }

    /**
     * Validate national ID uniqueness
     */
    private void validateNationalId(String nationalId, Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(HISDmlStatement.CHECK_NATIONAL_ID);
            pstmt.setString(1, nationalId);
            rs = pstmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "National ID already exists: " + nationalId);
            }
        } finally {
            DbUtils.closeAll(rs, pstmt, null);
        }
    }

    /**
     * Generate patient names
     */
    private PatientName generatePatientNames(CreatePatientRequest req) {
        String fullNameAr = String.join(" ",
                req.getFirstNameAr(),
                req.getFatherNameAr(),
                req.getGrandfatherNameAr() != null ? req.getGrandfatherNameAr() : "",
                req.getLastNameAr()).trim().replaceAll("\\s+", " ");

        String fullNameEn = String.join(" ",
                req.getFirstNameEn(),
                req.getFatherNameEn(),
                req.getGrandfatherNameEn() != null ? req.getGrandfatherNameEn() : "",
                req.getLastNameEn()).trim().replaceAll("\\s+", " ");

        String filterNameAr = fullNameAr.replaceAll("\\s+", "").toLowerCase();
        String filterNameEn = fullNameEn.replaceAll("\\s+", "").toLowerCase();

        return new PatientName(fullNameAr, fullNameEn, filterNameAr, filterNameEn);
    }

    /**
     * Inner class for patient name data
     */
    private static class PatientName {
        private final String fullNameAr;
        private final String fullNameEn;
        private final String filterNameAr;
        private final String filterNameEn;

        public PatientName(String fullNameAr, String fullNameEn,
                String filterNameAr, String filterNameEn) {
            this.fullNameAr = fullNameAr;
            this.fullNameEn = fullNameEn;
            this.filterNameAr = filterNameAr;
            this.filterNameEn = filterNameEn;
        }

        public String getFullNameAr() {
            return fullNameAr;
        }

        public String getFullNameEn() {
            return fullNameEn;
        }

        public String getFilterNameAr() {
            return filterNameAr;
        }

        public String getFilterNameEn() {
            return filterNameEn;
        }
    }

    @Transactional(readOnly = true)
    public PatientDto getPatientById(Long patientId) {
        // log.info("Getting patient with ID: {}", patientId);

        // Find patient in SYS_PATIENTS table
        PatientEntity patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Patient not found with ID: " + patientId));

        // Find patient details in SYS_PATIENT_DETAILS table
        PatientDetailsEntity details = patientDetailsRepository.findById(patientId)
                .orElse(null); // Details might be null if not created

        // // log.info("Found patient: {} {} (ID: {})",
        //         patient.getFirstNameE(), patient.getLastNameE(), patientId);

        // Map entities to DTO
        return mapToPatientDto(patient, details);
    }

    @Transactional(readOnly = true)
    public PatientDto getPatientByNumber(Long patientNo) {
        // log.info("Getting patient with patient number: {}", patientNo);

        // Find patient in SYS_PATIENTS
        PatientEntity patient = patientRepository.findByPatientNo(patientNo);
        if (patient == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Patient not found with patient number: " + patientNo);
        }

        // Find patient details
        PatientDetailsEntity details = patientDetailsRepository.findById(patient.getId())
                .orElse(null);

        // Map to DTO
        return mapToPatientDto(patient, details);
    }

    /**
     * Map PatientEntity and PatientDetailsEntity to PatientDto
     */
    private PatientDto mapToPatientDto(PatientEntity patient, PatientDetailsEntity details) {
        PatientDto dto = new PatientDto();

        // Basic patient info from SYS_PATIENTS
        dto.setPatientId(patient.getId());
        dto.setPatientNo(patient.getPatientNo());
        dto.setSiteId(patient.getSiteId());
        // Names from SYS_PATIENTS
        dto.setFirstNameAr(patient.getFirstNameA());
        dto.setFirstNameEn(patient.getFirstNameE());
        dto.setFatherNameAr(patient.getFatherNameA());
        dto.setFatherNameEn(patient.getFatherNameE());
        dto.setGrandfatherNameAr(patient.getGrandfatherNameA());
        dto.setGrandfatherNameEn(patient.getGrandfatherNameE());
        dto.setLastNameAr(patient.getLastNameA());
        dto.setLastNameEn(patient.getLastNameE());

        // Full names (already calculated and stored in database)
        dto.setFullNameAr(patient.getFullNameAr());
        dto.setFullNameEn(patient.getFullNameEng());

        // Demographics from SYS_PATIENTS
        dto.setGender(patient.getSex());
        dto.setDateOfBirth(patient.getDateOfBirth());
        dto.setPlaceOfBirth(patient.getBirthPlace());
        dto.setNationalNo(patient.getNationalIdNo());
        dto.setNationality(patient.getNationalValue());

        // Contact info from SYS_PATIENT_DETAILS (if exists)
        if (details != null) {
            dto.setMobile(details.getMobileNo());
            dto.setEmail(details.getEmail());
            dto.setAddress(details.getAddress());
            dto.setMaritalStatus(details.getMaritalStatusValue());
        } else {
            // Set default/empty values if details not found
            dto.setMobile("");
            dto.setEmail("");
            dto.setAddress("");
            dto.setMaritalStatus(null);
        }
        // dto.setFlagStatus(patient.getFileStatus());
        // Audit info from SYS_PATIENTS
        dto.setCreatedBy(patient.getCreatedBy());
        dto.setCreationDate(patient.getCreationDate());
        dto.setLastUpdatedBy(patient.getLastUpdatedBy());
        dto.setLastUpdatedDate(patient.getLastUpdatedDate());

        return dto;
    }

    @Transactional
    public void updatePatient(Long patientNo, UpdatePatientRequest req) {
        log.info("Updating patient with ID: {}", patientNo);

        /* ================= VALIDATE REQUIRED FIELDS FOR UPDATE ================= */
        validateUpdateFields(req);

        /* ================= FIND EXISTING PATIENT ================= */
        PatientEntity patient = patientRepository.findByPatientNo(patientNo);
     if (patient == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Patient not found with patient number: " + patientNo);
        }
                
        PatientDetailsEntity details = patientDetailsRepository.findById(patient.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Patient details not found for ID: " + patientNo));

        /*
         * ================= CHECK FOR DUPLICATE (EXCLUDING CURRENT PATIENT)
         * =================
         */
        // Always check if any key fields are being updated
        checkPatientDuplicateForUpdate(patient.getId(), req, patient, details);

        /* ================= UPDATE PATIENT ENTITY (SYS_PATIENTS) ================= */
        boolean nameChanged = updatePatientEntity(patient, req);

        /*
         * ================= UPDATE PATIENT DETAILS ENTITY (SYS_PATIENT_DETAILS)
         * =================
         */
        updatePatientDetailsEntity(details, req);

        /* ================= SAVE UPDATES ================= */
        patientRepository.save(patient);
        patientDetailsRepository.save(details);

        log.info("Patient updated successfully - ID: {}", patientNo);
    }

    /**
     * Validate fields for update
     */
    private void validateUpdateFields(UpdatePatientRequest req) {
        StringBuilder errors = new StringBuilder();

        // Trim whitespace from all string fields
        trimStringFields(req);

        // First Name (Arb/Eng): Validate if provided
        if (req.getFirstNameAr() != null && req.getFirstNameAr().isEmpty()) {
            errors.append("First Name (Arabic) cannot be empty. ");
        }
        if (req.getFirstNameEn() != null && req.getFirstNameEn().isEmpty()) {
            errors.append("First Name (English) cannot be empty. ");
        }

        // Father Name (Arb/Eng): Validate if provided
        if (req.getFatherNameAr() != null && req.getFatherNameAr().isEmpty()) {
            errors.append("Father Name (Arabic) cannot be empty. ");
        }
        if (req.getFatherNameEn() != null && req.getFatherNameEn().isEmpty()) {
            errors.append("Father Name (English) cannot be empty. ");
        }

        // Last Name (Arb/Eng): Validate if provided
        if (req.getLastNameAr() != null && req.getLastNameAr().isEmpty()) {
            errors.append("Last Name (Arabic) cannot be empty. ");
        }
        if (req.getLastNameEn() != null && req.getLastNameEn().isEmpty()) {
            errors.append("Last Name (English) cannot be empty. ");
        }

        // Grandfather names: Can be empty (null them out if empty string)
        if (req.getGrandfatherNameAr() != null && req.getGrandfatherNameAr().isEmpty()) {
            req.setGrandfatherNameAr(null);
        }
        if (req.getGrandfatherNameEn() != null && req.getGrandfatherNameEn().isEmpty()) {
            req.setGrandfatherNameEn(null);
        }

        // Date of Birth: Validate if provided
        if (req.getDateOfBirth() != null) {
            if (req.getDateOfBirth().isAfter(LocalDate.now())) {
                errors.append("Date of Birth cannot be in the future. ");
            }
            if (req.getDateOfBirth().isBefore(LocalDate.now().minusYears(150))) {
                errors.append("Date of Birth appears to be invalid. ");
            }
        }

        // Mobile Phone: Validate format if provided
        if (req.getMobile() != null && !req.getMobile().isEmpty()) {
            String mobile = req.getMobile();
            if (!mobile.matches("^[0-9]{4,}$")) {
                errors.append("Mobile number must be more than 3 digits. ");
            }
        }

        // Gender: Validate if provided
        if (req.getGender() != null && req.getGender() != 1 && req.getGender() != 2) {
            errors.append("Gender must be 1 (Male) or 2 (Female). ");
        }

        // Email: Validate format if provided
        if (req.getEmail() != null && !req.getEmail().isEmpty()) {
            String email = req.getEmail();
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                errors.append("Email format is invalid. ");
            }
        }

        // National ID: Validate format if provided
        if (req.getNationalNo() != null && !req.getNationalNo().isEmpty()) {
            String nationalNo = req.getNationalNo();
            if (!nationalNo.matches("^[0-9]{10,15}$")) {
                errors.append("National ID must be 10-15 digits. ");
            }
        }

        // If there are validation errors, throw exception
        if (errors.length() > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Validation failed: " + errors.toString().trim());
        }
    }

    /**
     * Trim whitespace from all string fields
     */
    private void trimStringFields(UpdatePatientRequest req) {
        if (req.getFirstNameAr() != null)
            req.setFirstNameAr(req.getFirstNameAr().trim());
        if (req.getFirstNameEn() != null)
            req.setFirstNameEn(req.getFirstNameEn().trim());
        if (req.getFatherNameAr() != null)
            req.setFatherNameAr(req.getFatherNameAr().trim());
        if (req.getFatherNameEn() != null)
            req.setFatherNameEn(req.getFatherNameEn().trim());
        if (req.getGrandfatherNameAr() != null)
            req.setGrandfatherNameAr(req.getGrandfatherNameAr().trim());
        if (req.getGrandfatherNameEn() != null)
            req.setGrandfatherNameEn(req.getGrandfatherNameEn().trim());
        if (req.getLastNameAr() != null)
            req.setLastNameAr(req.getLastNameAr().trim());
        if (req.getLastNameEn() != null)
            req.setLastNameEn(req.getLastNameEn().trim());
        if (req.getMobile() != null)
            req.setMobile(req.getMobile().trim());
        if (req.getEmail() != null)
            req.setEmail(req.getEmail().trim());
        if (req.getAddress() != null)
            req.setAddress(req.getAddress().trim());
        if (req.getPlaceOfBirth() != null)
            req.setPlaceOfBirth(req.getPlaceOfBirth().trim());
        if (req.getNationalNo() != null)
            req.setNationalNo(req.getNationalNo().trim());
    }

    /**
     * Check for duplicate patient excluding current patient
     */
    private void checkPatientDuplicateForUpdate(Long patientId, UpdatePatientRequest req,
            PatientEntity patient, PatientDetailsEntity details) {
        try {
            // Only check if critical fields are being changed
            boolean shouldCheck = false;

            // Get values to check (use existing if not provided in request)
            String firstName = req.getFirstNameEn() != null ? req.getFirstNameEn() : patient.getFirstNameE();
            String fatherName = req.getFatherNameEn() != null ? req.getFatherNameEn() : patient.getFatherNameE();
            String grandfatherName = req.getGrandfatherNameEn() != null ? req.getGrandfatherNameEn()
                    : patient.getGrandfatherNameE();
            String lastName = req.getLastNameEn() != null ? req.getLastNameEn() : patient.getLastNameE();
            String mobile = req.getMobile() != null ? req.getMobile() : details.getMobileNo();
            LocalDate dateOfBirth = req.getDateOfBirth() != null ? req.getDateOfBirth() : patient.getDateOfBirth();

            // String duplicateResult = patientCheckService.checkPatientDuplicateForUpdate(
            // patientId,
            // firstName,
            // fatherName,
            // grandfatherName,
            // lastName,
            // mobile,
            // dateOfBirth != null ? Timestamp.valueOf(dateOfBirth.atStartOfDay()) : null);

            // if (duplicateResult != null && !"Y".equalsIgnoreCase(duplicateResult.trim()))
            // {
            // throw new ResponseStatusException(HttpStatus.CONFLICT, duplicateResult);
            // }
        } catch (Exception e) {
            log.warn("Error checking duplicate for update: {}", e.getMessage());
            // Continue with update if duplicate check fails
        }
    }

    /**
     * Update PatientEntity fields
     * Returns true if any name field changed
     */
    private boolean updatePatientEntity(PatientEntity patient, UpdatePatientRequest req) {
        boolean nameChanged = false;

        // Update basic info
        if (req.getFirstNameAr() != null) {
            nameChanged = nameChanged || !Objects.equals(req.getFirstNameAr(), patient.getFirstNameA());
            patient.setFirstNameA(req.getFirstNameAr());
        }
        if (req.getFirstNameEn() != null) {
            nameChanged = nameChanged || !Objects.equals(req.getFirstNameEn(), patient.getFirstNameE());
            patient.setFirstNameE(req.getFirstNameEn());
        }
        if (req.getFatherNameAr() != null) {
            nameChanged = nameChanged || !Objects.equals(req.getFatherNameAr(), patient.getFatherNameA());
            patient.setFatherNameA(req.getFatherNameAr());
        }
        if (req.getFatherNameEn() != null) {
            nameChanged = nameChanged || !Objects.equals(req.getFatherNameEn(), patient.getFatherNameE());
            patient.setFatherNameE(req.getFatherNameEn());
        }
        if (req.getGrandfatherNameAr() != null) {
            nameChanged = nameChanged || !Objects.equals(req.getGrandfatherNameAr(), patient.getGrandfatherNameA());
            patient.setGrandfatherNameA(req.getGrandfatherNameAr());
        }
        if (req.getGrandfatherNameEn() != null) {
            nameChanged = nameChanged || !Objects.equals(req.getGrandfatherNameEn(), patient.getGrandfatherNameE());
            patient.setGrandfatherNameE(req.getGrandfatherNameEn());
        }
        if (req.getLastNameAr() != null) {
            nameChanged = nameChanged || !Objects.equals(req.getLastNameAr(), patient.getLastNameA());
            patient.setLastNameA(req.getLastNameAr());
        }
        if (req.getLastNameEn() != null) {
            nameChanged = nameChanged || !Objects.equals(req.getLastNameEn(), patient.getLastNameE());
            patient.setLastNameE(req.getLastNameEn());
        }

        // Update demographics
        if (req.getGender() != null) {
            patient.setSex(req.getGender());
        }
        if (req.getDateOfBirth() != null) {
            patient.setDateOfBirth(req.getDateOfBirth());
        }
        if (req.getPlaceOfBirth() != null) {
            patient.setBirthPlace(req.getPlaceOfBirth());
        }
        if (req.getNationalNo() != null) {
            patient.setNationalIdNo(req.getNationalNo());
        }
        if (req.getNationality() != null) {
            patient.setNationalValue(req.getNationality());
        }

        // Update full names if any name changed
        if (nameChanged) {
            updateFullNames(patient);
        }

        // Update audit fields will be handled by @PreUpdate in entity
        return nameChanged;
    }

    /**
     * Update full names in patient entity
     */
    private void updateFullNames(PatientEntity patient) {
        // Generate Arabic full name
        String fullNameAr = String.join(" ",
                patient.getFirstNameA() != null ? patient.getFirstNameA() : "",
                patient.getFatherNameA() != null ? patient.getFatherNameA() : "",
                patient.getGrandfatherNameA() != null ? patient.getGrandfatherNameA() : "",
                patient.getLastNameA() != null ? patient.getLastNameA() : "").trim().replaceAll("\\s+", " ");

        // Generate English full name
        String fullNameEn = String.join(" ",
                patient.getFirstNameE() != null ? patient.getFirstNameE() : "",
                patient.getFatherNameE() != null ? patient.getFatherNameE() : "",
                patient.getGrandfatherNameE() != null ? patient.getGrandfatherNameE() : "",
                patient.getLastNameE() != null ? patient.getLastNameE() : "").trim().replaceAll("\\s+", " ");

        // Generate filter names (for search)
        String filterNameAr = fullNameAr.replaceAll("\\s+", "").toLowerCase();
        String filterNameEn = fullNameEn.replaceAll("\\s+", "").toLowerCase();

        patient.setFullNameAr(fullNameAr);
        patient.setFullNameEng(fullNameEn);
        patient.setFilterFullNameAr(filterNameAr);
        patient.setFilterFullNameEng(filterNameEn);
    }

    /**
     * Update PatientDetailsEntity fields
     */
    private void updatePatientDetailsEntity(PatientDetailsEntity details, UpdatePatientRequest req) {
        if (req.getMaritalStatus() != null) {
            details.setMaritalStatusValue(req.getMaritalStatus());
        }
        if (req.getMobile() != null) {
            details.setMobileNo(req.getMobile());
        }
        if (req.getEmail() != null) {
            details.setEmail(req.getEmail());
        }
        if (req.getAddress() != null) {
            details.setAddress(req.getAddress());
        }

        // Update audit fields will be handled by @PreUpdate in entity
    }

    //// Search patients method can be added here in the future
    ///
    @Transactional(readOnly = true)
    public List<PatientDto> searchPatientsByName(String searchText, Boolean isEnglish, Integer limit) {
        // Get site ID from authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long siteId = 1L; // Default site ID

        if (auth != null && auth.getPrincipal() instanceof EmployeePrincipal) {
            EmployeePrincipal principal = (EmployeePrincipal) auth.getPrincipal();
            siteId = principal.getSiteId();
            // log.info("Site ID for patient search: {}", siteId);
        } else {
            log.warn("No authenticated employee found or principal is not EmployeePrincipal");
        }

        // Prepare search text
        String input = searchText.trim();
        if (isEnglish) {
            input = input.toUpperCase();
        }

        // Build regex pattern
        String[] keywords = input.split("\\s+");
        StringBuilder regex = new StringBuilder("^\\s*");
        for (int i = 0; i < keywords.length; i++) {
            if (i > 0) {
                regex.append("\\s+");
            }
            regex.append(keywords[i]);
            regex.append("[^\\s]*");
        }
        regex.append(".*");

        // Set default limit if not provided
        if (limit == null || limit <= 0 || limit > 100) {
            limit = 100;
        }

        // log.info("Searching patients - regex: {}, isEnglish: {}, siteId: {}, limit: {}",
        //         regex.toString(), isEnglish, siteId, limit);

        // Search using repository with dynamic query
        List<PatientSearchResult> results = patientRepository.searchPatientsByNameDynamic(
                siteId,
                isEnglish ? 1 : 0, // Convert boolean to integer for SQL CASE
                regex.toString(),
                limit);

        // log.info("Found {} patients", results.size());

        // Map results to DTOs
        return results.stream()
                .map(this::mapSearchResultToDto)
                .collect(Collectors.toList());
    }

    /**
     * Map PatientSearchResult (from native query) to PatientDto
     */
    private PatientDto mapSearchResultToDto(PatientSearchResult result) {
        PatientDto dto = new PatientDto();

        // Basic patient info - convert BigDecimal to Long
        dto.setPatientId(result.getId() != null ? result.getId().longValue() : null);
        dto.setPatientNo(result.getPatientNo() != null ? result.getPatientNo().longValue() : null);
        dto.setSiteId(result.getSiteId() != null ? result.getSiteId().longValue() : null);

        // Names
        dto.setFirstNameAr(result.getFirstNameA());
        dto.setFirstNameEn(result.getFirstNameE());
        dto.setFatherNameAr(result.getFatherNameA());
        dto.setFatherNameEn(result.getFatherNameE());
        dto.setGrandfatherNameAr(result.getGrandfatherNameA());
        dto.setGrandfatherNameEn(result.getGrandfatherNameE());
        dto.setLastNameAr(result.getLastNameA());
        dto.setLastNameEn(result.getLastNameE());
        dto.setFullNameAr(result.getFullNameArb());
        dto.setFullNameEn(result.getFullNameEng());

        // Demographics
        dto.setGender(result.getSex() != null ? result.getSex().longValue() : null);

        // Date of birth - Timestamp to LocalDate
        if (result.getDateOfBirth() != null) {
            dto.setDateOfBirth(result.getDateOfBirth().toLocalDateTime().toLocalDate());
        }

        dto.setPlaceOfBirth(result.getBirthPlace());
        dto.setNationalNo(result.getNationalIdNo());
        dto.setNationality(result.getNationalValue() != null ? result.getNationalValue().longValue() : null);
        // dto.setFlagStatus(result.getFileStatus() != null ? result.getFileStatus().longValue() : null);

        // Contact info
        dto.setMobile(result.getMobileNo() != null ? result.getMobileNo() : "");
        dto.setEmail(result.getEmail() != null ? result.getEmail() : "");
        dto.setAddress(result.getAdress() != null ? result.getAdress() : "");
        dto.setMaritalStatus(
                result.getMaritalStatusValue() != null ? result.getMaritalStatusValue().longValue() : null);

        // Audit info
        dto.setCreatedBy(result.getCreatedBy());
        if (result.getCreationDate() != null) {
            dto.setCreationDate(result.getCreationDate().toLocalDateTime());
        }
        dto.setLastUpdatedBy(result.getLastUpdatedBy());
        if (result.getLastUpdatedDate() != null) {
            dto.setLastUpdatedDate(result.getLastUpdatedDate().toLocalDateTime());
        }

        return dto;
    }
}
