package com.demo.patient.controller;

import com.demo.patient.api.ActionResponse;
import com.demo.patient.api.PatientDetailResponse;
import com.demo.patient.api.PatientSummaryResponse;
import com.demo.patient.domain.PatientStatus;
import com.demo.patient.service.ActionService;
import com.demo.patient.service.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
public class PatientApiController {

    private final PatientService patientService;

    private final ActionService actionService;

    public PatientApiController(
            PatientService patientService,
            ActionService actionService
    ) {
        this.patientService = patientService;
        this.actionService = actionService;
    }

    @GetMapping
    public Page<PatientSummaryResponse> getPatients(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) PatientStatus status,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate invitedFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate invitedTo,
            Pageable pageable) {
        return patientService.searchPatients(search, status, module, invitedFrom, invitedTo, pageable);
    }

    @GetMapping("/{id}")
    public PatientDetailResponse getPatient(
            @PathVariable UUID id
    ) {
        return patientService.getPatient(id);
    }

    @GetMapping("/{id}/actions")
    public Page<ActionResponse> getPatientActions(
            @PathVariable UUID id,
            Pageable pageable
    ) {
        return actionService.getActions(id, pageable);
    }
}