package com.plazoleta.microservicio_plazoleta.infrastructure.input.rest;

import com.plazoleta.microservicio_plazoleta.application.dto.response.EmployeeEfficiencyResponseDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.OrderDurationResponseDto;
import com.plazoleta.microservicio_plazoleta.application.handler.impl.OrderAnalyticsHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/owners/restaurants/{restaurantId}/analytics")
public class OwnerAnalyticsController {

    private final OrderAnalyticsHandler handler;

    @Operation(summary = "Eficiencia de pedidos",
            description = "Devuelve la duración de cada pedido (desde que inicia hasta que termina) para un restaurante",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida (parámetros de paginación incorrectos)"),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida o token inválido/expirado."),
            @ApiResponse(responseCode = "403", description = "No eres el propietario del restaurante"),
            @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    })
    @GetMapping("/orders/efficiency")
    public ResponseEntity<List<OrderDurationResponseDto>> orderDurations(
            @PathVariable Long restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    ) {
        return ResponseEntity.ok(handler.listOrderDurations(restaurantId, page, size));
    }

    @Operation(summary = "Ranking de eficiencia de empleados",
            description = "Devuelve un ranking de empleados con estadísticas de tiempo promedio, mediana y p90 de preparación de pedidos",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida (parámetros incorrectos)"),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida o token inválido/expirado."),
            @ApiResponse(responseCode = "403", description = "No eres el propietario del restaurante"),
            @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    })
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

