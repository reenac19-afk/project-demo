package com.demo.patient.service;

import com.demo.patient.exception.PatientNotFoundException;
import com.demo.patient.repository.ActionRepository;
import com.demo.patient.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActionServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private ActionRepository actionRepository;

    @InjectMocks
    private ActionService actionService;

    @Test
    void shouldThrowExceptionWhenPatientNotFound() {

        UUID id = UUID.randomUUID();

        when(patientRepository.findByPublicId(id))
                .thenReturn(Optional.empty());

        assertThrows(
                PatientNotFoundException.class,
                () -> actionService.getActions(
                        id,
                        Pageable.unpaged()
                )
        );

        verify(patientRepository).findByPublicId(id);
        verifyNoInteractions(actionRepository);
    }
}