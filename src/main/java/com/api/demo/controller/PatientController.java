package com.api.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api.demo.dto.CreatePatientRequest;
import com.api.demo.dto.PatientDto;
import com.api.demo.dto.SearchPatientRequest;
import com.api.demo.dto.UpdatePatientRequest;
import com.api.demo.service.PatientService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/patients")
@AllArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Patient Management", description = "Operations for managing patients")
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/{patientId}")
    public PatientDto getPatientById(@PathVariable Long patientId) {
        return patientService.getPatientById(patientId);
    }
    @GetMapping("/number/{patientNo}")
    public PatientDto getPatientByNumber(@PathVariable Long patientNo) {
        return patientService.getPatientByNumber(patientNo);
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


      @PostMapping("/search")
    public ResponseEntity<List<PatientDto>> searchPatients(@RequestBody SearchPatientRequest request) {
          if( request.getLimit() == null || request.getLimit() <=0 || request.getLimit() > 500) {
              request.setLimit(250); // Default to 250 if invalid limit is provided
          }
        List<PatientDto> patients = patientService.searchPatientsByName(
            request.getSearchText(), 
            request.getIsEnglish(),
            request.getLimit()  
        );
        return ResponseEntity.ok(patients);
    }
}
