package com.demo.patient.repository;

import com.demo.patient.domain.Action;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActionRepository extends JpaRepository<Action, Long> {

    Page<Action> findByPatientEntityId(
            Long patientEntityId,
            Pageable pageable
    );

    @Query("""
        SELECT a.moduleId, COUNT(a)
        FROM Action a
        WHERE a.patient.entityId = :patientEntityId
        GROUP BY a.moduleId
        """)
    List<Object[]> countActionsByModule(
            @Param("patientEntityId") Long patientEntityId
    );
}