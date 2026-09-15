package com.demo.patient.repository;

import com.demo.patient.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface PatientRepository
        extends JpaRepository<Patient, Long>,
        JpaSpecificationExecutor<Patient> {

    Optional<Patient> findByPublicId(UUID publicId);

    long countByWhenDischargedIsNotNull();

    long countByWhenDischargedIsNullAndWhenRegisteredIsNotNull();

    long countByWhenDischargedIsNullAndWhenRegisteredIsNull();
}