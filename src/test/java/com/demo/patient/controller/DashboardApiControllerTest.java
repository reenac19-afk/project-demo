package com.demo.patient.controller;

import com.demo.patient.api.DashboardResponse;
import com.demo.patient.service.DashboardService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DashboardApiControllerTest {

    @Test
    void shouldReturnDashboard() {

        DashboardService dashboardService = mock(DashboardService.class);

        DashboardResponse expected =
                new DashboardResponse(3, 80, 17, 100);

        when(dashboardService.getDashboard()).thenReturn(expected);

        DashboardApiController controller =
                new DashboardApiController(dashboardService);

        DashboardResponse actual = controller.dashboard();

        assertEquals(expected, actual);
        verify(dashboardService).getDashboard();
    }
}