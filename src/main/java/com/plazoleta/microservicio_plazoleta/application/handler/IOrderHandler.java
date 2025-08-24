package com.plazoleta.microservicio_plazoleta.application.handler;

import com.plazoleta.microservicio_plazoleta.application.dto.request.CreateOrderRequestDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.CreateOrderResponseDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.OrderResponseDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.TraceLogResponseDto;

import java.util.List;

public interface IOrderHandler {
    CreateOrderResponseDto create(CreateOrderRequestDto request);
    OrderResponseDto cancel(Long orderId);
    List<TraceLogResponseDto> getMyOrderTrace(Long orderId);
}
