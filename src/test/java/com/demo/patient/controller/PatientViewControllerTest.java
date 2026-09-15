package com.demo.patient.controller;

import com.demo.patient.api.ActionResponse;
import com.demo.patient.api.PatientDetailResponse;
import com.demo.patient.api.PatientSummaryResponse;
import com.demo.patient.service.ActionService;
import com.demo.patient.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.ui.Model;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PatientViewControllerTest {

    @Test
    void shouldReturnPatientsView() {

        PatientService patientService = mock(PatientService.class);
        ActionService actionService = mock(ActionService.class);
        Model model = mock(Model.class);

        Page<PatientSummaryResponse> patients =
                new PageImpl<>(List.of());

        when(patientService.searchPatients(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any()
        )).thenReturn(patients);

        PatientViewController controller =
                new PatientViewController(
                        patientService,
                        actionService
                );

        String view = controller.patients(
                null,
                null,
                null,
                null,
                null,
                0,
                20,
                "familyName",
                "asc",
                model
        );

        assertEquals("patients", view);

        verify(model).addAttribute("patients", patients);
        verify(model).addAttribute("sort", "familyName");
        verify(model).addAttribute("direction", "asc");
    }

    @Test
    void shouldReturnPatientDetailView() {

        PatientService patientService = mock(PatientService.class);
        ActionService actionService = mock(ActionService.class);
        Model model = mock(Model.class);

        UUID id = UUID.randomUUID();

        PatientDetailResponse patient =
                mock(PatientDetailResponse.class);

        Page<ActionResponse> actions =
                new PageImpl<>(List.of());

        when(patientService.getPatient(id))
                .thenReturn(patient);

        when(actionService.getActions(eq(id), any()))
                .thenReturn(actions);

        PatientViewController controller =
                new PatientViewController(
                        patientService,
                        actionService
                );

        String view =
                controller.patientDetail(id, 0, 10, model);

        assertEquals("patient-detail", view);

        verify(model).addAttribute("patient", patient);
        verify(model).addAttribute("actions", actions);
    }
}