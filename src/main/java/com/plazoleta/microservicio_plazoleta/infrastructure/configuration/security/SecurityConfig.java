package com.plazoleta.microservicio_plazoleta.infrastructure.configuration.security;

import com.plazoleta.microservicio_plazoleta.infrastructure.exceptionhandler.CustomAccessDeniedHandler;
import com.plazoleta.microservicio_plazoleta.infrastructure.exceptionhandler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.plazoleta.microservicio_plazoleta.domain.util.Constantes.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtFilter;
    private final CustomAuthenticationEntryPoint entryPoint;
    private final CustomAccessDeniedHandler accessDenied;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(reg -> reg
                        .requestMatchers("/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/restaurants/saveRestaurant").hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/v1/dishes/saveDish").hasRole(ROLE_OWNER)
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/dishes/updateDish/**").hasRole(ROLE_OWNER)
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/dishes/{id}/status").hasRole(ROLE_OWNER)
                        .requestMatchers(HttpMethod.POST, "/api/v1/restaurantEmployee/saveRestaurantEmployee").hasRole(ROLE_OWNER)
                        .requestMatchers(HttpMethod.GET, "/api/v1/restaurants").hasAnyRole(ROLE_CLIENT)
                        .anyRequest().permitAll()
                )
                .exceptionHandling(e -> e.authenticationEntryPoint(entryPoint).accessDeniedHandler(accessDenied))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
