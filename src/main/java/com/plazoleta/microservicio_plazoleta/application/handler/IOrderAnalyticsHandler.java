package com.plazoleta.microservicio_plazoleta.application.handler;

import com.plazoleta.microservicio_plazoleta.application.dto.response.EmployeeEfficiencyResponseDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.OrderDurationResponseDto;

import java.util.List;

public interface IOrderAnalyticsHandler {
    List<OrderDurationResponseDto> listOrderDurations(Long restaurantId, int page, int size);
    List<EmployeeEfficiencyResponseDto> ranking(Long restaurantId, int page, int size, int minOrders);
}
