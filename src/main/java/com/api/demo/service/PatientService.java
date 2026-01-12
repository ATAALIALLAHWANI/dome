package com.api.demo.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.api.demo.entity.PatientEntity;
import com.api.demo.repository.PatientRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientEntity getPatientById(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Patient not found with ID: " + patientId
                ));
    }
}
