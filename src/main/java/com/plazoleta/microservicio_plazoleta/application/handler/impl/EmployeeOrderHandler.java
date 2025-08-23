package com.plazoleta.microservicio_plazoleta.application.handler.impl;

import com.plazoleta.microservicio_plazoleta.application.dto.response.OrderResponseDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.PageResponse;
import com.plazoleta.microservicio_plazoleta.application.handler.IEmployeeOrderHandler;
import com.plazoleta.microservicio_plazoleta.application.mapper.IEmployeeOrderResponseMapper;
import com.plazoleta.microservicio_plazoleta.domain.api.IEmployeeOrderServicePort;
import com.plazoleta.microservicio_plazoleta.domain.model.Dish;
import com.plazoleta.microservicio_plazoleta.domain.model.Order;
import com.plazoleta.microservicio_plazoleta.domain.model.OrderItem;
import com.plazoleta.microservicio_plazoleta.domain.model.OrderStatus;
import com.plazoleta.microservicio_plazoleta.domain.spi.IDishPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.util.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeOrderHandler implements IEmployeeOrderHandler {
    private final IEmployeeOrderServicePort employeeOrderServicePort;
    private final IDishPersistencePort dishPersistencePort;
    private final IEmployeeOrderResponseMapper employeeOrderResponseMapper;

    @Override
    public PageResponse<OrderResponseDto> listByStatus(OrderStatus status, int page, int size) {

        PageResult<Order> pageResult = employeeOrderServicePort.listOrdersByStatus(status, page, size);

        var dishIds = pageResult.getItems().stream()
                .flatMap(o -> o.getItems().stream())
                .map(OrderItem::getDishId)
                .collect(Collectors.toSet());

        Map<Long, Dish> dishesById = dishIds.isEmpty()
                ? Collections.emptyMap()
                : dishPersistencePort.findByIds(dishIds);

        return employeeOrderResponseMapper.toPage(pageResult, dishesById);
    }

    @Override
    public OrderResponseDto assignSelf(Long orderId) {
        Order order = employeeOrderServicePort.assignSelfToOrder(orderId);
        Map<Long, Dish> dishesById = dishItems(order);
        return employeeOrderResponseMapper.toResponse(order, dishesById);
    }

    @Override
    public OrderResponseDto markOrderAsReady(Long orderId) {
        return employeeOrderResponseMapper.toResponse(employeeOrderServicePort.markOrderAsReady(orderId), Collections.emptyMap());
    }

    @Override
    public OrderResponseDto deliver(Long orderId, String pin) {
        Order delivered = employeeOrderServicePort.deliverOrder(orderId, pin);
        Map<Long, Dish> dishesById = dishItems(delivered);
        return employeeOrderResponseMapper.toResponse(delivered, dishesById);
    }

    private Map<Long, Dish>  dishItems(Order order){
        Set<Long> dishIds = order.getItems() == null ? Set.of()
                : order.getItems()
                .stream()
                .map(OrderItem::getDishId)
                .collect(Collectors.toSet());

        return dishIds.isEmpty()
                ? Collections.emptyMap()
                : dishPersistencePort.findByIds(dishIds);
    }


}

