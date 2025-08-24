package com.plazoleta.microservicio_plazoleta.application.dto.response;

public record EmployeeEfficiencyResponseDto(
        Long employeeId,
        long avgSeconds,
        long medianSeconds,
        long p90Seconds,
        long orders,
        double avgMinutes
) {}

