package com.demo.patient.repository;

import com.demo.patient.domain.Action;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionRepository extends JpaRepository<Action, Long> {

    Page<Action> findByPatientEntityId(
            Long patientEntityId,
            Pageable pageable
    );
}