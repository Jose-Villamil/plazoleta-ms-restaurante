package com.plazoleta.microservicio_plazoleta.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TraceLogResponseDto {
    private Long orderId;
    private Long clientId;
    private String clientEmail;
    private Long employeeId;
    private String employeeEmail;
    private String oldStatus;
    private String newStatus;
    private Instant at;
}
