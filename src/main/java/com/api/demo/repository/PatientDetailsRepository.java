package com.api.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.api.demo.entity.PatientDetailsEntity;

@Repository
public interface PatientDetailsRepository extends JpaRepository<PatientDetailsEntity, Long> {
        PatientDetailsEntity findByPatientId(Long patientId);
        

}
