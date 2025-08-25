package com.plazoleta.microservicio_plazoleta.infrastructure.input.rest;

import com.plazoleta.microservicio_plazoleta.application.dto.request.CreateOrderRequestDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.CreateOrderResponseDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.OrderResponseDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.TraceLogResponseDto;
import com.plazoleta.microservicio_plazoleta.application.handler.impl.OrderHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderHandler orderHandler;

    @Operation(summary = "Crear pedido",
            description = "Crea un nuevo pedido para un cliente",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido creado"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida (p. ej., pedido vacío o datos incorrectos)"),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida o token inválido/expirado."),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para crear pedidos"),
            @ApiResponse(responseCode = "409", description = "El pedido ya existe o viola una restricción de negocio")
    })
    @PostMapping
    public ResponseEntity<CreateOrderResponseDto> create(@RequestBody CreateOrderRequestDto request) {
        var resp = orderHandler.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }


    @Operation(summary = "Cancelar pedido",
            description = "Cancela un pedido si todavía está pendiente",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido cancelado"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida o token inválido/expirado."),
            @ApiResponse(responseCode = "403", description = "No puedes cancelar este pedido"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponseDto> cancel(@PathVariable Long id) {
        OrderResponseDto dto = orderHandler.cancel(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Consultar trazabilidad de mi pedido",
            description = "Devuelve el historial de estados de un pedido del cliente autenticado",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida o token inválido/expirado."),
            @ApiResponse(responseCode = "403", description = "No puedes consultar pedidos de otros clientes"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @GetMapping("/{orderId}/trace")
    public ResponseEntity<List<TraceLogResponseDto>> getMyTrace(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderHandler.getMyOrderTrace(orderId));
    }

}

