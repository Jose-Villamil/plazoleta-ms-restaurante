package com.plazoleta.microservicio_plazoleta.infrastructure.input.rest;

import com.plazoleta.microservicio_plazoleta.application.dto.request.CreateOrderRequestDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.CreateOrderResponseDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.OrderResponseDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.PageResponse;
import com.plazoleta.microservicio_plazoleta.application.handler.impl.EmployeeOrderHandler;
import com.plazoleta.microservicio_plazoleta.application.handler.impl.OrderHandler;
import com.plazoleta.microservicio_plazoleta.domain.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderHandler orderHandler;
    private final EmployeeOrderHandler employeeOrderHandler;

    @PostMapping
    public ResponseEntity<CreateOrderResponseDto> create(@RequestBody CreateOrderRequestDto request) {
        var resp = orderHandler.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping
    public ResponseEntity<PageResponse<OrderResponseDto>> list(
            @RequestParam("status") OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10", name = "size") int size
    ) {
        var resp = employeeOrderHandler.listByStatus(status, page, size);
        return ResponseEntity.ok(resp);
    }
}

