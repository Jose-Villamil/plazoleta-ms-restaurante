package com.plazoleta.microservicio_plazoleta.application.dto.response;

import java.time.Instant;

public record OrderDurationResponseDto(
        Long orderId,
        Long chefId,
        Instant startedAt,
        Instant finishedAt,
        long durationSeconds,
        double durationMinutes,
        double durationHours
) {}

