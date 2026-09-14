package com.demo.patient.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "action")
public class Action {

    @Id
    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "id", nullable = false, unique = true)
    private UUID publicId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_entity_id", nullable = false)
    private Patient patient;

    @Column(name = "when_recorded", nullable = false)
    private LocalDateTime whenRecorded;

    @Column(nullable = false)
    private String activity;

    @Column(nullable = false)
    private String context;

    @Column(name = "module_id", nullable = false)
    private String moduleId;

    @Column(name = "entity_created", nullable = false)
    private LocalDateTime entityCreated;

    @Column(name = "entity_updated", nullable = false)
    private LocalDateTime entityUpdated;

    @Column(name = "entity_version", nullable = false)
    private Long entityVersion;

    protected Action() {
    }

    public Long getEntityId() {
        return entityId;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public Patient getPatient() {
        return patient;
    }

    public LocalDateTime getWhenRecorded() {
        return whenRecorded;
    }

    public String getActivity() {
        return activity;
    }

    public String getContext() {
        return context;
    }

    public String getModuleId() {
        return moduleId;
    }

    public LocalDateTime getEntityCreated() {
        return entityCreated;
    }

    public LocalDateTime getEntityUpdated() {
        return entityUpdated;
    }

    public Long getEntityVersion() {
        return entityVersion;
    }
}