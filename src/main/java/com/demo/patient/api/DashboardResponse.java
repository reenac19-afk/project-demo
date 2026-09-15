package com.demo.patient.api;

public record DashboardResponse(
        long invited,
        long registered,
        long discharged,
        long total
) {
}