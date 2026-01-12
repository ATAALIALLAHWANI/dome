package com.api.demo.controller;

import org.springframework.web.bind.annotation.*;

import com.api.demo.dto.PatientDto;
import com.api.demo.service.PatientService;

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
}
