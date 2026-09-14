package com.demo.patient.repository;

import com.demo.patient.domain.Patient;
import org.springframework.data.jpa.domain.Specification;

public final class PatientSpecification {

    private PatientSpecification() {
    }

    public static Specification<Patient> containsText(String search) {

        return (root, query, criteriaBuilder) -> {

            if (search == null || search.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            String pattern = "%" + search.trim().toLowerCase() + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("givenName")),
                            pattern
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("familyName")),
                            pattern
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("nhsNumber")),
                            pattern
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("hospitalId")),
                            pattern
                    )
            );
        };
    }
}