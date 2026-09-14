package com.demo.patient.service;

import com.demo.patient.api.ActionResponse;
import com.demo.patient.domain.Patient;
import com.demo.patient.exception.PatientNotFoundException;
import com.demo.patient.repository.ActionRepository;
import com.demo.patient.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ActionService {

    private final PatientRepository patientRepository;
    private final ActionRepository actionRepository;

    public ActionService(
            PatientRepository patientRepository,
            ActionRepository actionRepository
    ) {
        this.patientRepository = patientRepository;
        this.actionRepository = actionRepository;
    }

    public Page<ActionResponse> getActions(
            UUID patientId,
            Pageable pageable
    ) {
        Patient patient = patientRepository.findByPublicId(patientId)
                .orElseThrow(() ->
                        new PatientNotFoundException(patientId)
                );

        return actionRepository
                .findByPatientEntityId(
                        patient.getEntityId(),
                        pageable
                )
                .map(action -> new ActionResponse(
                        action.getPublicId(),
                        action.getWhenRecorded(),
                        action.getActivity(),
                        action.getContext(),
                        action.getModuleId()
                ));
    }
}