package com.demo.patient.service;

import com.demo.patient.domain.Patient;
import com.demo.patient.domain.PatientStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.OptionalInt;

@Service
public class PatientDerivedDataService {

    public PatientStatus deriveStatus(Patient patient) {

        if (patient.getWhenDischarged() != null) {
            return PatientStatus.DISCHARGED;
        }

        if (patient.getWhenRegistered() != null) {
            return PatientStatus.REGISTERED;
        }

        return PatientStatus.INVITED;
    }

    public OptionalInt calculateAge(Patient patient, LocalDate asOfDate) {

        LocalDate dateOfBirth = patient.getDateOfBirth();

        if (dateOfBirth == null || dateOfBirth.isAfter(asOfDate)) {
            return OptionalInt.empty();
        }

        return OptionalInt.of(
                Period.between(dateOfBirth, asOfDate).getYears()
        );
    }
}