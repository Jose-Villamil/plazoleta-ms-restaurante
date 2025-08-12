package com.plazoleta.microservicio_plazoleta.infrastructure.configuration.security;


import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignGlobalConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            String authHeader = "Basic " +
                    java.util.Base64.getEncoder()
                            .encodeToString("admin:admin123".getBytes());

            requestTemplate.header("Authorization", authHeader);
        };
    }
}
