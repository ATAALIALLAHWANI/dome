package com.api.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.api.demo.repository.UserRepository;
import com.api.demo.repository.PatientRepository;
import com.api.demo.repository.PatientDetailsRepository;
import com.api.demo.entity.UserEntity;
import com.api.demo.entity.PatientEntity;
import com.api.demo.dto.LoginResponseDto;
import com.api.demo.dto.PatientDto;
import com.api.demo.entity.PatientDetailsEntity;
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientDetailsRepository patientDetailsRepository;

    public LoginResponseDto login(String username, String password) {
        // Find active user
        UserEntity user = userRepository.findByUsernameAndPasswordAndActive(username, password, 1L)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        // Fetch patient
        PatientEntity patient = patientRepository.findById(user.getPatientId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Patient not found"));

        // Fetch patient details
        PatientDetailsEntity details = patientDetailsRepository.findById(user.getPatientId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Patient details not found"));

        // Map to DTO
        PatientDto patientDto = new PatientDto();
        patientDto.setPatientId(patient.getPatientId());
        patientDto.setFullNameAr(patient.getFullNameAr());
        patientDto.setFullNameEn(patient.getFullNameEn());
        patientDto.setDateOfBirth(patient.getDateOfBirth());
        patientDto.setNationalId(patient.getNationalId());
        patientDto.setMobile(details.getMobile());
        patientDto.setEmail(details.getEmail());

        LoginResponseDto response = new LoginResponseDto();
        response.setUserId(user.getUserId());
        response.setPatient(patientDto);

        return response;
    }
}