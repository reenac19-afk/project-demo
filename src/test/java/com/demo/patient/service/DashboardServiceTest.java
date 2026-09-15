package com.demo.patient.service;

import com.demo.patient.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void shouldReturnDashboardCounts() {

        when(patientRepository.countByWhenDischargedIsNotNull())
                .thenReturn(17L);

        when(patientRepository.countByWhenDischargedIsNullAndWhenRegisteredIsNotNull())
                .thenReturn(80L);

        when(patientRepository.countByWhenDischargedIsNullAndWhenRegisteredIsNull())
                .thenReturn(3L);

        var result = dashboardService.getDashboard();

        assertEquals(100L, result.total());
        assertEquals(80L, result.registered());
        assertEquals(3L, result.invited());
        assertEquals(17L, result.discharged());
    }
}