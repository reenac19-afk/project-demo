package com.demo.patient.controller;

import com.demo.patient.api.PatientSummaryResponse;
import com.demo.patient.domain.PatientStatus;
import com.demo.patient.service.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/patients")
public class PatientApiController {

    private final PatientService patientService;

    public PatientApiController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public Page<PatientSummaryResponse> getPatients(@RequestParam(required = false) String search, @RequestParam(required = false) PatientStatus status, @RequestParam(required = false) String module, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate invitedFrom, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate invitedTo, Pageable pageable) {
        return patientService.searchPatients(search, status, module, invitedFrom, invitedTo, pageable);
    }
}