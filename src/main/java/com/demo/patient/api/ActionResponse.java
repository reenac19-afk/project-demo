package com.demo.patient.api;

import java.time.LocalDateTime;
import java.util.UUID;

public record ActionResponse(
        UUID id,
        LocalDateTime whenRecorded,
        String activity,
        String context,
        String moduleId
) {
}