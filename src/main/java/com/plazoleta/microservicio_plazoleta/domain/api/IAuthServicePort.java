package com.plazoleta.microservicio_plazoleta.domain.api;

public interface IAuthServicePort {
    Long getAuthenticatedUserId();
    String getAuthenticatedEmail();
}
