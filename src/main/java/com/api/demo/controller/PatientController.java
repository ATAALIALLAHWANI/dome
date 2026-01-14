package com.api.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api.demo.dto.CreatePatientRequest;
import com.api.demo.dto.PatientDto;
import com.api.demo.dto.UpdatePatientRequest;
import com.api.demo.service.PatientService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/patients")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/{patientId}")
    public PatientDto getPatientById(@PathVariable Long patientId) {
        return patientService.getPatientById(patientId);
    }


     @PostMapping
    public ResponseEntity<Long> createPatient(
            @Valid @RequestBody CreatePatientRequest request) {

        Long patientId = patientService.createPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(patientId);
    }

     @PutMapping("/{patientId}")
    public ResponseEntity<Void> updatePatient(
            @PathVariable Long patientId,
            @Valid @RequestBody UpdatePatientRequest request) {
        patientService.updatePatient(patientId, request);
        return ResponseEntity.ok().build();
    }


    
}
