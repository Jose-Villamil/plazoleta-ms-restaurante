package com.plazoleta.microservicio_plazoleta.domain.model;

import java.time.Instant;

public class Tracelog {
    Long orderId;
    Long clientId;
    String clientEmail;
    Long employeeId;
    String employeeEmail;
    String oldStatus;
    String newStatus;
    Instant at;

    public Tracelog() {
    }

    public Tracelog(Long orderId, Long clientId, String clientEmail, Long employeeId, String employeeEmail, String oldStatus, String newStatus, Instant at) {
        this.orderId = orderId;
        this.clientId = clientId;
        this.clientEmail = clientEmail;
        this.employeeId = employeeId;
        this.employeeEmail = employeeEmail;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.at = at;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public Instant getAt() {
        return at;
    }

    public void setAt(Instant at) {
        this.at = at;
    }
}

