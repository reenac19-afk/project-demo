package com.demo.patient.controller;

import com.demo.patient.domain.PatientStatus;
import com.demo.patient.service.ActionService;
import com.demo.patient.service.PatientService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.UUID;

@Controller
public class PatientViewController {

    private final PatientService patientService;
    private final ActionService actionService;

    public PatientViewController(
            PatientService patientService,
            ActionService actionService
    ) {
        this.patientService = patientService;
        this.actionService = actionService;
    }

    @GetMapping("/patients")
    public String patients(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) PatientStatus status,
            @RequestParam(required = false) String module,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate invitedFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate invitedTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "familyName") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            Model model
    ) {

        Sort.Direction sortDirection =
                direction.equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        var pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sort)
        );

        var patients = patientService.searchPatients(
                search,
                status,
                module,
                invitedFrom,
                invitedTo,
                pageable
        );

        model.addAttribute("patients", patients);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("module", module);
        model.addAttribute("invitedFrom", invitedFrom);
        model.addAttribute("invitedTo", invitedTo);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        model.addAttribute("statuses", PatientStatus.values());

        return "patients";
    }

    @GetMapping("/patients/{id}")
    public String patientDetail(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {

        var patient = patientService.getPatient(id);

        var actions = actionService.getActions(
                id,
                PageRequest.of(
                        page,
                        size,
                        Sort.by(Sort.Direction.ASC, "whenRecorded")
                )
        );

        model.addAttribute("patient", patient);
        model.addAttribute("actions", actions);

        return "patient-detail";
    }
}