package com.plazoleta.microservicio_plazoleta.infrastructure.configuration.security;

import com.plazoleta.microservicio_plazoleta.domain.api.IAuthServicePort;
import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JwtAuthServiceAdapter implements IAuthServicePort {
    @Override
    public Long getAuthenticatedUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new DomainException("No autenticado");
        Object details = auth.getDetails();
        if (details instanceof Map<?,?> map && map.get("uid") != null) {
            return Long.valueOf(String.valueOf(map.get("uid")));
        }
        throw new DomainException("No se encontró el uid en el token");
    }
}
