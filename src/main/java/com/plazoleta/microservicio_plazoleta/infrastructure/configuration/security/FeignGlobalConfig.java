package com.plazoleta.microservicio_plazoleta.infrastructure.configuration.security;


import feign.RequestInterceptor;
import org.apache.hc.core5.http.HttpHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignGlobalConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            var attrs = RequestContextHolder.getRequestAttributes();
            if (attrs instanceof ServletRequestAttributes sra) {
                String auth = sra.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
                if (auth != null) template.header(HttpHeaders.AUTHORIZATION, auth);
            }
        };
    }
}
