package com.api.demo.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.api.demo.dto.PatientDto;
import com.api.demo.entity.PatientDetailsEntity;
import com.api.demo.entity.PatientEntity;
import com.api.demo.repository.PatientDetailsRepository;
import com.api.demo.repository.PatientRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientDetailsRepository patientDetailsRepository;

    public PatientDto getPatientById(Long patientId) {

        PatientEntity patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Patient not found with ID: " + patientId
                ));

        PatientDetailsEntity details = patientDetailsRepository.findByPatientId(patientId);
        
        // Map to DTO
        PatientDto dto = new PatientDto();
        dto.setPatientId(patient.getPatientId());
        dto.setFullNameAr(patient.getFullNameAr());
        dto.setFullNameEn(patient.getFullNameEn());
        dto.setDateOfBirth(patient.getDateOfBirth());
        dto.setNationalId(patient.getNationalId());
        
        if (details != null) { // sometimes details might not exist
            dto.setMobile(details.getMobile());
            dto.setEmail(details.getEmail());
        }

        return dto;
    }
}
