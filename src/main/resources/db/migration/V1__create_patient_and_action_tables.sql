CREATE TABLE patient (
                         entity_id BIGINT PRIMARY KEY,
                         id UUID NOT NULL UNIQUE,

                         given_name VARCHAR(255) NOT NULL,
                         family_name VARCHAR(255) NOT NULL,
                         title VARCHAR(255),
                         gender VARCHAR(255),
                         date_of_birth DATE,

                         hospital_id VARCHAR(255) UNIQUE,
                         nhs_number VARCHAR(255) UNIQUE,

                         when_invited TIMESTAMP(6) NOT NULL,
                         when_registered TIMESTAMP(6),
                         when_discharged TIMESTAMP(6),

                         entity_created TIMESTAMP(6) NOT NULL,
                         entity_updated TIMESTAMP(6) NOT NULL,
                         entity_version BIGINT NOT NULL,

                         CONSTRAINT patient_gender_check
                             CHECK (gender IN ('MALE', 'FEMALE'))
);

CREATE TABLE action (
                        entity_id BIGINT PRIMARY KEY,
                        id UUID NOT NULL UNIQUE,

                        patient_entity_id BIGINT NOT NULL,

                        when_recorded TIMESTAMP(6) NOT NULL,
                        activity VARCHAR(255) NOT NULL,
                        context VARCHAR(255) NOT NULL,
                        module_id VARCHAR(255) NOT NULL,

                        entity_created TIMESTAMP(6) NOT NULL,
                        entity_updated TIMESTAMP(6) NOT NULL,
                        entity_version BIGINT NOT NULL,

                        CONSTRAINT action_patient_fk
                            FOREIGN KEY (patient_entity_id)
                                REFERENCES patient(entity_id),

                        CONSTRAINT action_module_id_check
                            CHECK (module_id IN ('ASSESSMENT', 'DIARY', 'PROGRAMME'))
);