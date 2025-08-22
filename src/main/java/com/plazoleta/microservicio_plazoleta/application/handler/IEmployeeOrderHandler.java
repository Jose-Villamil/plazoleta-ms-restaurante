package com.plazoleta.microservicio_plazoleta.application.handler;

import com.plazoleta.microservicio_plazoleta.application.dto.response.OrderResponseDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.PageResponse;
import com.plazoleta.microservicio_plazoleta.domain.model.OrderStatus;

public interface IEmployeeOrderHandler {
    PageResponse<OrderResponseDto> listByStatus(OrderStatus status, int page, int size);
}
