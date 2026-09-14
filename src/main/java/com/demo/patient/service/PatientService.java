package com.demo.patient.service;

import com.demo.patient.api.PatientDetailResponse;
import com.demo.patient.api.PatientSummaryResponse;
import com.demo.patient.domain.Patient;
import com.demo.patient.domain.PatientStatus;
import com.demo.patient.exception.PatientNotFoundException;
import com.demo.patient.repository.ActionRepository;
import com.demo.patient.repository.PatientRepository;
import com.demo.patient.repository.PatientSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PatientService {
    private static final Logger log =
            LoggerFactory.getLogger(PatientService.class);

    private final PatientRepository patientRepository;
    private final PatientDerivedDataService derivedDataService;
    private final ActionRepository actionRepository;

    public PatientService(
            PatientRepository patientRepository,
            PatientDerivedDataService derivedDataService,
            ActionRepository actionRepository
    ) {
        this.patientRepository = patientRepository;
        this.derivedDataService = derivedDataService;
        this.actionRepository = actionRepository;
    }

    public Page<PatientSummaryResponse> searchPatients(
            String search,
            PatientStatus status,
            String module,
            LocalDate invitedFrom,
            LocalDate invitedTo,
            Pageable pageable
    ) {

        log.info(
                "Searching patients page={} size={} sort={} searchProvided={} status={} module={} invitedFromProvided={} invitedToProvided={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort(),
                search != null && !search.isBlank(),
                status,
                module,
                invitedFrom != null,
                invitedTo != null
        );

        var specification =
                PatientSpecification.containsText(search)
                        .and(PatientSpecification.hasStatus(status))
                        .and(PatientSpecification.hasModule(module))
                        .and(PatientSpecification.invitedFrom(invitedFrom))
                        .and(PatientSpecification.invitedTo(invitedTo));

        var result = patientRepository
                .findAll(specification, pageable)
                .map(this::toSummaryResponse);

        log.info(
                "Patient search completed results={} totalResults={}",
                result.getNumberOfElements(),
                result.getTotalElements()
        );

        return result;
    }


    private PatientSummaryResponse toSummaryResponse(Patient patient) {

        var age = derivedDataService.calculateAge(
                patient,
                LocalDate.now()
        );

        return new PatientSummaryResponse(
                patient.getPublicId(),
                patient.getGivenName(),
                patient.getFamilyName(),
                patient.getDateOfBirth(),
                age.isPresent() ? age.getAsInt() : null,
                derivedDataService.deriveStatus(patient),
                patient.getHospitalId(),
                patient.getNhsNumber()
        );
    }

    public PatientDetailResponse getPatient(UUID publicId) {

        Patient patient = patientRepository.findByPublicId(publicId)
                .orElseThrow(() ->
                        new PatientNotFoundException(publicId)
                );

        var age = derivedDataService.calculateAge(
                patient,
                LocalDate.now()
        );

        Map<String, Long> engagementByModule =
                actionRepository.countActionsByModule(patient.getEntityId())
                        .stream()
                        .collect(Collectors.toMap(
                                row -> (String) row[0],
                                row -> (Long) row[1]
                        ));

        return new PatientDetailResponse(
                patient.getPublicId(),
                patient.getGivenName(),
                patient.getFamilyName(),
                patient.getTitle(),
                patient.getGender(),
                patient.getDateOfBirth(),
                age.isPresent() ? age.getAsInt() : null,
                patient.getHospitalId(),
                patient.getNhsNumber(),
                derivedDataService.deriveStatus(patient),
                patient.getWhenInvited(),
                patient.getWhenRegistered(),
                patient.getWhenDischarged(),
                engagementByModule
        );
    }
}