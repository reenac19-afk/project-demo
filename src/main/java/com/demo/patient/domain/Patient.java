package com.demo.patient.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "patient")
public class Patient {

    @Id
    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "id", nullable = false, unique = true)
    private UUID publicId;

    @Column(name = "given_name", nullable = false)
    private String givenName;

    @Column(name = "family_name", nullable = false)
    private String familyName;

    private String title;

    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "hospital_id")
    private String hospitalId;

    @Column(name = "nhs_number")
    private String nhsNumber;

    @Column(name = "when_invited", nullable = false)
    private LocalDateTime whenInvited;

    @Column(name = "when_registered")
    private LocalDateTime whenRegistered;

    @Column(name = "when_discharged")
    private LocalDateTime whenDischarged;

    @Column(name = "entity_created", nullable = false)
    private LocalDateTime entityCreated;

    @Column(name = "entity_updated", nullable = false)
    private LocalDateTime entityUpdated;

    @Column(name = "entity_version", nullable = false)
    private Long entityVersion;

    public Patient() {
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public void setPublicId(UUID publicId) {
        this.publicId = publicId;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getNhsNumber() {
        return nhsNumber;
    }

    public void setNhsNumber(String nhsNumber) {
        this.nhsNumber = nhsNumber;
    }

    public LocalDateTime getWhenInvited() {
        return whenInvited;
    }

    public void setWhenInvited(LocalDateTime whenInvited) {
        this.whenInvited = whenInvited;
    }

    public LocalDateTime getWhenRegistered() {
        return whenRegistered;
    }

    public void setWhenRegistered(LocalDateTime whenRegistered) {
        this.whenRegistered = whenRegistered;
    }

    public LocalDateTime getWhenDischarged() {
        return whenDischarged;
    }

    public void setWhenDischarged(LocalDateTime whenDischarged) {
        this.whenDischarged = whenDischarged;
    }

    public LocalDateTime getEntityCreated() {
        return entityCreated;
    }

    public void setEntityCreated(LocalDateTime entityCreated) {
        this.entityCreated = entityCreated;
    }

    public LocalDateTime getEntityUpdated() {
        return entityUpdated;
    }

    public void setEntityUpdated(LocalDateTime entityUpdated) {
        this.entityUpdated = entityUpdated;
    }

    public Long getEntityVersion() {
        return entityVersion;
    }

    public void setEntityVersion(Long entityVersion) {
        this.entityVersion = entityVersion;
    }
}