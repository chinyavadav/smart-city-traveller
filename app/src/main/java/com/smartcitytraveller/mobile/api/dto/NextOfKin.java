package com.smartcitytraveller.mobile.api.dto;

import java.util.UUID;

public class NextOfKin {
    private UUID id;
    private UUID userId;
    private String relationship;
    private String firstName;
    private String lastName;
    private String msisdn;
    private String created;
    private String updated;

    public NextOfKin( ) {
    }

    public NextOfKin(UUID userId, String relationship, String firstName, String lastName, String msisdn) {
        this.userId = userId;
        this.relationship = relationship;
        this.firstName = firstName;
        this.lastName = lastName;
        this.msisdn = msisdn;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }
}
