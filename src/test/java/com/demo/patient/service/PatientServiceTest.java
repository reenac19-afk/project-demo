package com.demo.patient.service;

import com.demo.patient.domain.Patient;
import com.demo.patient.domain.PatientStatus;
import com.demo.patient.exception.PatientNotFoundException;
import com.demo.patient.repository.ActionRepository;
import com.demo.patient.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientDerivedDataService derivedDataService;

    @Mock
    private ActionRepository actionRepository;

    @InjectMocks
    private PatientService patientService;

    @Test
    void shouldReturnPatientDetails() {

        UUID id = UUID.randomUUID();

        Patient patient = mock(Patient.class);

        when(patientRepository.findByPublicId(id))
                .thenReturn(Optional.of(patient));

        when(patient.getPublicId()).thenReturn(id);
        when(patient.getGivenName()).thenReturn("John");
        when(patient.getFamilyName()).thenReturn("Smith");
        when(patient.getEntityId()).thenReturn(1L);

        when(derivedDataService.calculateAge(
                eq(patient),
                any(LocalDate.class)
        )).thenReturn(OptionalInt.of(40));

        when(derivedDataService.deriveStatus(patient))
                .thenReturn(PatientStatus.REGISTERED);

        when(actionRepository.countActionsByModule(1L))
                .thenReturn(List.of());

        var result = patientService.getPatient(id);

        assertEquals(id, result.id());
        assertEquals("John", result.givenName());
        assertEquals("Smith", result.familyName());
        assertEquals(40, result.age());
        assertEquals(PatientStatus.REGISTERED, result.status());
    }

    @Test
    void shouldThrowExceptionWhenPatientNotFound() {

        UUID id = UUID.randomUUID();

        when(patientRepository.findByPublicId(id))
                .thenReturn(Optional.empty());

        assertThrows(
                PatientNotFoundException.class,
                () -> patientService.getPatient(id)
        );

        verify(patientRepository).findByPublicId(id);

        verifyNoInteractions(
                derivedDataService,
                actionRepository
        );
    }
}