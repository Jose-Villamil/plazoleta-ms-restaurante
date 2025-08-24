package com.plazoleta.microservicio_plazoleta.domain.api;

import java.time.Instant;
import java.util.List;

public interface IOrderAnalyticsServicePort {
    List<OrderDuration> listOrderDurations(Long restaurantId, int page, int size);
    List<EmployeeEfficiency> employeeRanking(Long restaurantId, int page, int size, int minOrders);

    record OrderDuration(Long orderId, Long chefId, Instant startedAt, Instant finishedAt, long durationSeconds) {}
    record EmployeeEfficiency(Long employeeId, long avgSeconds, long medianSeconds, long p90Seconds, long orders) {}
}

