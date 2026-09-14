package com.demo.patient.api;

import com.demo.patient.domain.PatientStatus;

import java.time.LocalDate;
import java.util.UUID;

public record PatientSummaryResponse(
        UUID id,
        String givenName,
        String familyName,
        LocalDate dateOfBirth,
        Integer age,
        PatientStatus status,
        String hospitalId,
        String nhsNumber
) {
}