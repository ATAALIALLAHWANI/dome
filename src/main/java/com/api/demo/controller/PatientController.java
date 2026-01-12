package com.api.demo.controller;

import org.springframework.web.bind.annotation.*;

import com.api.demo.entity.PatientEntity;
import com.api.demo.service.PatientService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/patients")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/{patientId}")
    public PatientEntity getPatientById(@PathVariable Long patientId) {
        System.out.println("Fetching patient with ID: " + patientId);
        return patientService.getPatientById(patientId);
    }
}
