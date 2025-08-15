package com.plazoleta.microservicio_plazoleta.infrastructure.configuration.security;

import com.plazoleta.microservicio_plazoleta.domain.api.IAuthServicePort;
import org.springframework.stereotype.Component;

@Component
public class DummyAuthServiceAdapter implements IAuthServicePort {
    @Override
    public Long getAuthenticatedUserId() {
        // Simulación: siempre devuelve el ID del propietario 1
        return 1L;
    }
}
