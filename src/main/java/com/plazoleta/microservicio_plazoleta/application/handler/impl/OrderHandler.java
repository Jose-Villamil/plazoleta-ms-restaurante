package com.plazoleta.microservicio_plazoleta.application.handler.impl;

import com.plazoleta.microservicio_plazoleta.application.dto.request.CreateOrderRequestDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.CreateOrderResponseDto;
import com.plazoleta.microservicio_plazoleta.application.handler.IOrderHandler;
import com.plazoleta.microservicio_plazoleta.application.mapper.IOrderRequestMapper;
import com.plazoleta.microservicio_plazoleta.application.mapper.IOrderResponseMapper;
import com.plazoleta.microservicio_plazoleta.domain.api.IOrderServicePort;
import com.plazoleta.microservicio_plazoleta.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderHandler implements IOrderHandler {

    private final IOrderServicePort service;
    private final IOrderRequestMapper reqMapper;
    private final IOrderResponseMapper resMapper;


    public CreateOrderResponseDto create(CreateOrderRequestDto request) {
        Order order = reqMapper.toOrder(request);
        Order saved = service.saveOrder(order);
        return resMapper.toResponse(saved);
    }
}

