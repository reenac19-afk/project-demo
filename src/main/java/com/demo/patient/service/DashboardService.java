package com.demo.patient.service;

import com.demo.patient.api.DashboardResponse;
import com.demo.patient.repository.PatientRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final PatientRepository patientRepository;

    public DashboardService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public DashboardResponse getDashboard() {

        long invited =
                patientRepository.countByWhenDischargedIsNullAndWhenRegisteredIsNull();

        long registered =
                patientRepository.countByWhenDischargedIsNullAndWhenRegisteredIsNotNull();

        long discharged =
                patientRepository.countByWhenDischargedIsNotNull();

        return new DashboardResponse(
                invited,
                registered,
                discharged,
                invited + registered + discharged
        );
    }
}
