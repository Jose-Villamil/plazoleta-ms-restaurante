package com.plazoleta.microservicio_plazoleta.application.handler.impl;

import com.plazoleta.microservicio_plazoleta.application.dto.response.EmployeeEfficiencyResponseDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.OrderDurationResponseDto;
import com.plazoleta.microservicio_plazoleta.application.handler.IOrderAnalyticsHandler;
import com.plazoleta.microservicio_plazoleta.domain.api.IOrderAnalyticsServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderAnalyticsHandler implements IOrderAnalyticsHandler {

    private final IOrderAnalyticsServicePort orderAnalyticsServicePort;

    @Override
    public List<OrderDurationResponseDto> listOrderDurations(Long restaurantId, int page, int size) {
        return orderAnalyticsServicePort.listOrderDurations(restaurantId, page, size).stream()
                .map(d -> new OrderDurationResponseDto(
                        d.orderId(), d.chefId(), d.startedAt(), d.finishedAt(),
                        d.durationSeconds(), d.durationSeconds() / 60.0, (double) d.durationSeconds() / 3600
                ))
                .toList();
    }

    @Override
    public List<EmployeeEfficiencyResponseDto> ranking(Long restaurantId, int page, int size, int minOrders) {
        return orderAnalyticsServicePort.employeeRanking(restaurantId, page, size, minOrders).stream()
                .map(e -> new EmployeeEfficiencyResponseDto(
                        e.employeeId(), e.avgSeconds(), e.medianSeconds(), e.p90Seconds(), e.orders(),
                        e.avgSeconds() / 60.0
                ))
                .toList();
    }
}

