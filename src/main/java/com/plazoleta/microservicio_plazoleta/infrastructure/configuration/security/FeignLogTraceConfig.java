package com.plazoleta.microservicio_plazoleta.infrastructure.configuration.security;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class FeignLogTraceConfig {

    @Value("${trazabilidad.internal-token}")
    private String internalToken;

    @Bean
    public RequestInterceptor traceAuthInterceptor() {
        return template -> template.header("X-Internal-Token", internalToken);
    }
}

