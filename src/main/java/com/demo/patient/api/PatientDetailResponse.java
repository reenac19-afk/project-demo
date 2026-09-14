package com.demo.patient.api;

import com.demo.patient.domain.PatientStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PatientDetailResponse(
        UUID id,
        String givenName,
        String familyName,
        String title,
        String gender,
        LocalDate dateOfBirth,
        Integer age,
        String hospitalId,
        String nhsNumber,
        PatientStatus status,
        LocalDateTime whenInvited,
        LocalDateTime whenRegistered,
        LocalDateTime whenDischarged
) {
}