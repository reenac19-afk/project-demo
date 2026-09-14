package com.demo.patient.repository;

import com.demo.patient.domain.Action;
import com.demo.patient.domain.Patient;
import com.demo.patient.domain.PatientStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class PatientSpecification {

    public static final String WHEN_DISCHARGED = "whenDischarged";
    public static final String WHEN_REGISTERED = "whenRegistered";
    public static final String WHEN_INVITED = "whenInvited";

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

    public static Specification<Patient> hasStatus(PatientStatus status) {

        return (root, query, criteriaBuilder) -> {

            if (status == null) {
                return criteriaBuilder.conjunction();
            }

            return switch (status) {

                case DISCHARGED ->
                        criteriaBuilder.isNotNull(root.get(WHEN_DISCHARGED));

                case REGISTERED ->
                        criteriaBuilder.and(
                                criteriaBuilder.isNull(root.get(WHEN_DISCHARGED)),
                                criteriaBuilder.isNotNull(root.get(WHEN_REGISTERED))
                        );

                case INVITED ->
                        criteriaBuilder.and(
                                criteriaBuilder.isNull(root.get(WHEN_DISCHARGED)),
                                criteriaBuilder.isNull(root.get(WHEN_REGISTERED))
                        );
            };
        };
    }

    public static Specification<Patient> invitedFrom(LocalDate from) {

        return (root, query, criteriaBuilder) -> {

            if (from == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get(WHEN_INVITED),
                    from.atStartOfDay()
            );
        };
    }

    public static Specification<Patient> invitedTo(LocalDate to) {

        return (root, query, criteriaBuilder) -> {

            if (to == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.lessThan(
                    root.get(WHEN_INVITED),
                    to.plusDays(1).atStartOfDay()
            );
        };
    }

    public static Specification<Patient> hasModule(String module) {

        return (root, query, criteriaBuilder) -> {

            if (module == null || module.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            var subquery = query.subquery(Long.class);
            var action = subquery.from(Action.class);

            subquery.select(action.get("patient").get("entityId"));

            subquery.where(
                    criteriaBuilder.equal(
                            action.get("patient").get("entityId"),
                            root.get("entityId")
                    ),
                    criteriaBuilder.equal(
                            action.get("moduleId"),
                            module.toUpperCase()
                    )
            );

            return criteriaBuilder.exists(subquery);
        };
    }
}