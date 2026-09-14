package com.demo.patient.service;

import com.demo.patient.domain.Patient;
import com.demo.patient.domain.PatientStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PatientDerivedDataServiceTest {

    private final PatientDerivedDataService service =
            new PatientDerivedDataService();

    @Test
    void patientWithOnlyInvitationIsInvited() {

        Patient patient = new Patient();
        patient.setWhenInvited(LocalDateTime.of(2025, 1, 1, 12, 0));

        assertEquals(
                PatientStatus.INVITED,
                service.deriveStatus(patient)
        );
    }

    @Test
    void registeredPatientIsRegistered() {

        Patient patient = new Patient();
        patient.setWhenInvited(LocalDateTime.of(2025, 1, 1, 12, 0));
        patient.setWhenRegistered(LocalDateTime.of(2025, 1, 2, 12, 0));

        assertEquals(
                PatientStatus.REGISTERED,
                service.deriveStatus(patient)
        );
    }

    @Test
    void dischargedPatientIsDischarged() {

        Patient patient = new Patient();
        patient.setWhenInvited(LocalDateTime.of(2025, 1, 1, 12, 0));
        patient.setWhenRegistered(LocalDateTime.of(2025, 1, 2, 12, 0));
        patient.setWhenDischarged(LocalDateTime.of(2025, 2, 1, 12, 0));

        assertEquals(
                PatientStatus.DISCHARGED,
                service.deriveStatus(patient)
        );
    }

    @Test
    void dischargedPatientWithoutRegistrationIsStillDischarged() {

        Patient patient = new Patient();
        patient.setWhenInvited(LocalDateTime.of(2025, 1, 1, 12, 0));
        patient.setWhenRegistered(null);
        patient.setWhenDischarged(LocalDateTime.of(2025, 2, 1, 12, 0));

        assertEquals(
                PatientStatus.DISCHARGED,
                service.deriveStatus(patient)
        );
    }

    @Test
    void calculatesAgeWhenBirthdayHasPassed() {

        Patient patient = new Patient();
        patient.setDateOfBirth(LocalDate.of(1980, 5, 10));

        var age = service.calculateAge(
                patient,
                LocalDate.of(2026, 9, 13)
        );

        assertTrue(age.isPresent());
        assertEquals(46, age.getAsInt());
    }

    @Test
    void calculatesAgeBeforeBirthday() {

        Patient patient = new Patient();
        patient.setDateOfBirth(LocalDate.of(1980, 12, 10));

        var age = service.calculateAge(
                patient,
                LocalDate.of(2026, 9, 13)
        );

        assertTrue(age.isPresent());
        assertEquals(45, age.getAsInt());
    }

    @Test
    void missingDateOfBirthReturnsEmptyAge() {

        Patient patient = new Patient();

        var age = service.calculateAge(
                patient,
                LocalDate.of(2026, 9, 13)
        );

        assertTrue(age.isEmpty());
    }

    @Test
    void futureDateOfBirthReturnsEmptyAge() {

        Patient patient = new Patient();
        patient.setDateOfBirth(LocalDate.of(2030, 1, 1));

        var age = service.calculateAge(
                patient,
                LocalDate.of(2026, 9, 13)
        );

        assertTrue(age.isEmpty());
    }
}