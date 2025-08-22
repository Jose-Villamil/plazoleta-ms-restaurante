package com.plazoleta.microservicio_plazoleta.infrastructure.input.rest;

import com.plazoleta.microservicio_plazoleta.application.dto.request.CreateOrderRequestDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.CreateOrderResponseDto;
import com.plazoleta.microservicio_plazoleta.application.handler.impl.OrderHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderHandler handler;

    @PostMapping
    public ResponseEntity<CreateOrderResponseDto> create(@RequestBody CreateOrderRequestDto request) {
        var resp = handler.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }
}

