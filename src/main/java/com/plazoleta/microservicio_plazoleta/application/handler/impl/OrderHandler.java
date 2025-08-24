package com.plazoleta.microservicio_plazoleta.application.handler.impl;

import com.plazoleta.microservicio_plazoleta.application.dto.request.CreateOrderRequestDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.CreateOrderResponseDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.OrderResponseDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.TraceLogResponseDto;
import com.plazoleta.microservicio_plazoleta.application.handler.IOrderHandler;
import com.plazoleta.microservicio_plazoleta.application.mapper.IEmployeeOrderResponseMapper;
import com.plazoleta.microservicio_plazoleta.application.mapper.IOrderRequestMapper;
import com.plazoleta.microservicio_plazoleta.application.mapper.IOrderResponseMapper;
import com.plazoleta.microservicio_plazoleta.application.mapper.ITraceLogResponseMapper;
import com.plazoleta.microservicio_plazoleta.domain.api.IOrderServicePort;
import com.plazoleta.microservicio_plazoleta.domain.model.Dish;
import com.plazoleta.microservicio_plazoleta.domain.model.Order;
import com.plazoleta.microservicio_plazoleta.domain.model.OrderItem;
import com.plazoleta.microservicio_plazoleta.domain.spi.IDishPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderHandler implements IOrderHandler {

    private final IOrderServicePort orderServicePort;
    private final IOrderRequestMapper orderRequestMapper;
    private final IOrderResponseMapper orderResponseMapper;
    private final IEmployeeOrderResponseMapper employeeOrderResponseMapper;
    private final IDishPersistencePort dishPersistencePort;
    private final ITraceLogResponseMapper traceLogResponseMapper;

    public CreateOrderResponseDto create(CreateOrderRequestDto request) {
        Order order = orderRequestMapper.toOrder(request);
        Order saved = orderServicePort.saveOrder(order);
        return orderResponseMapper.toResponse(saved);
    }

    @Override
    public OrderResponseDto cancel(Long orderId) {
        Order updated = orderServicePort.cancelOrder(orderId);

        Set<Long> dishIds =
                updated.getItems() == null ? Set.of()
                : updated.getItems().stream().map(OrderItem::getDishId).collect(Collectors.toSet());

        Map<Long, Dish> dishesById = dishIds.isEmpty()
                ? Collections.emptyMap()
                : dishPersistencePort.findByIds(dishIds);

        return employeeOrderResponseMapper.toResponse(updated, dishesById);
    }

    @Override
    public List<TraceLogResponseDto> getMyOrderTrace(Long orderId) {
        return traceLogResponseMapper.toDtoList(orderServicePort.getMyOrderTrace(orderId));
    }
}

