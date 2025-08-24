package com.plazoleta.microservicio_plazoleta.infrastructure.input.rest;

import com.plazoleta.microservicio_plazoleta.application.dto.request.DeliverOrderRequestDto;
import com.plazoleta.microservicio_plazoleta.application.dto.request.RestaurantEmployeeRequestDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.OrderResponseDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.PageResponse;
import com.plazoleta.microservicio_plazoleta.application.handler.impl.EmployeeOrderHandler;
import com.plazoleta.microservicio_plazoleta.application.handler.impl.RestaurantEmployeHandler;
import com.plazoleta.microservicio_plazoleta.domain.model.OrderStatus;
import com.plazoleta.microservicio_plazoleta.infrastructure.configuration.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/restaurantEmployee")
@RequiredArgsConstructor
public class RestaurantEmployeeController {
    private final RestaurantEmployeHandler restaurantEmployeHandler;
    private final EmployeeOrderHandler employeeOrderHandler;

    @PostMapping("saveRestaurantEmployee")
    public ResponseEntity<Map<String, String>> saveEmployeeByRestaurant(@RequestBody RestaurantEmployeeRequestDto restaurantEmployeeRequestDto) {
        restaurantEmployeHandler.saveRestaurantEmployee(restaurantEmployeeRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap(Constants.MESSAGE, Constants.RESTAURANT_CREATED));
    }

    @GetMapping
    public ResponseEntity<PageResponse<OrderResponseDto>> listOrderByStatus(
            @RequestParam("status") OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10", name = "size") int size
    ) {
        var resp = employeeOrderHandler.listByStatus(status, page, size);
        return ResponseEntity.ok(resp);
    }

    @PatchMapping("/{orderId}/assign")
    public ResponseEntity<OrderResponseDto> assignSelfOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(employeeOrderHandler.assignSelf(orderId));
    }

    @PatchMapping("/{id}/ready")
    public ResponseEntity<OrderResponseDto> markReady(@PathVariable Long id) {
        OrderResponseDto dto = employeeOrderHandler.markOrderAsReady(id);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{id}/deliver")
    public ResponseEntity<OrderResponseDto> deliver(
            @PathVariable Long id,
            @RequestBody DeliverOrderRequestDto body
    ) {
        OrderResponseDto dto = employeeOrderHandler.deliver(id, body.getPin());
        return ResponseEntity.ok(dto);
    }

}
