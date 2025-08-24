package com.plazoleta.microservicio_plazoleta.infrastructure.input.rest;

import com.plazoleta.microservicio_plazoleta.application.dto.response.EmployeeEfficiencyResponseDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.OrderDurationResponseDto;
import com.plazoleta.microservicio_plazoleta.application.handler.impl.OrderAnalyticsHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/owners/restaurants/{restaurantId}/analytics")
public class OwnerAnalyticsController {

    private final OrderAnalyticsHandler handler;

    @GetMapping("/orders/efficiency")
    public ResponseEntity<List<OrderDurationResponseDto>> orderDurations(
            @PathVariable Long restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    ) {
        return ResponseEntity.ok(handler.listOrderDurations(restaurantId, page, size));
    }

    @GetMapping("/employees/ranking")
    public ResponseEntity<List<EmployeeEfficiencyResponseDto>> employeesRanking(
            @PathVariable Long restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "3") int minOrders
    ) {
        return ResponseEntity.ok(handler.ranking(restaurantId, page, size, minOrders));
    }
}

