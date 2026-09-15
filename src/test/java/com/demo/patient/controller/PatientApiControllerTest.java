package com.demo.patient.controller;

import com.demo.patient.api.ActionResponse;
import com.demo.patient.api.PatientDetailResponse;
import com.demo.patient.api.PatientSummaryResponse;
import com.demo.patient.service.ActionService;
import com.demo.patient.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class PatientApiControllerTest {

    @Test
    void shouldReturnPatients() {

        PatientService patientService = mock(PatientService.class);
        ActionService actionService = mock(ActionService.class);

        Page<PatientSummaryResponse> expected =
                new PageImpl<>(List.of());

        when(patientService.searchPatients(
                null, null, null, null, null, Pageable.unpaged()
        )).thenReturn(expected);

        PatientApiController controller =
                new PatientApiController(patientService, actionService);

        Page<PatientSummaryResponse> actual =
                controller.getPatients(
                        null, null, null, null, null,
                        Pageable.unpaged()
                );

        assertSame(expected, actual);
    }

    @Test
    void shouldReturnPatient() {

        PatientService patientService = mock(PatientService.class);
        ActionService actionService = mock(ActionService.class);

        UUID id = UUID.randomUUID();

        PatientDetailResponse expected =
                mock(PatientDetailResponse.class);

        when(patientService.getPatient(id)).thenReturn(expected);

        PatientApiController controller =
                new PatientApiController(patientService, actionService);

        PatientDetailResponse actual = controller.getPatient(id);

        assertSame(expected, actual);
        verify(patientService).getPatient(id);
    }

    @Test
    void shouldReturnPatientActions() {

        PatientService patientService = mock(PatientService.class);
        ActionService actionService = mock(ActionService.class);

        UUID id = UUID.randomUUID();

        Page<ActionResponse> expected =
                new PageImpl<>(List.of());

        when(actionService.getActions(id, Pageable.unpaged()))
                .thenReturn(expected);

        PatientApiController controller =
                new PatientApiController(patientService, actionService);

        Page<ActionResponse> actual =
                controller.getPatientActions(id, Pageable.unpaged());

        assertSame(expected, actual);
        verify(actionService).getActions(id, Pageable.unpaged());
    }
}