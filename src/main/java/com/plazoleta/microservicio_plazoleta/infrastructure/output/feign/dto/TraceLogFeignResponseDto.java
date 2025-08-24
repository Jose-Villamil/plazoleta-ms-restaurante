package com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TraceLogFeignResponseDto {
    private Long orderId;
    private Long clientId;
    private String clientEmail;
    private Long employeeId;
    private String employeeEmail;
    private String oldStatus;
    private String newStatus;
    private Instant at;
}
