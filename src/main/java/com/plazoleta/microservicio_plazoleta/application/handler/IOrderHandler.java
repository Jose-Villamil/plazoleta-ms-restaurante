package com.plazoleta.microservicio_plazoleta.application.handler;

import com.plazoleta.microservicio_plazoleta.application.dto.request.CreateOrderRequestDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.CreateOrderResponseDto;

public interface IOrderHandler {
    CreateOrderResponseDto create(CreateOrderRequestDto request);
}
