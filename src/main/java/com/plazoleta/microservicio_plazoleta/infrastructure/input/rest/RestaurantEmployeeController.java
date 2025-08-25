package com.plazoleta.microservicio_plazoleta.infrastructure.input.rest;

import com.plazoleta.microservicio_plazoleta.application.dto.request.DeliverOrderRequestDto;
import com.plazoleta.microservicio_plazoleta.application.dto.request.RestaurantEmployeeRequestDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.OrderResponseDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.PageResponse;
import com.plazoleta.microservicio_plazoleta.application.handler.impl.EmployeeOrderHandler;
import com.plazoleta.microservicio_plazoleta.application.handler.impl.RestaurantEmployeHandler;
import com.plazoleta.microservicio_plazoleta.domain.model.OrderStatus;
import com.plazoleta.microservicio_plazoleta.infrastructure.configuration.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    @Operation(summary = "Registrar empleado en restaurante",
            description = "Asigna un empleado a un restaurante",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Empleado registrado"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida o token inválido/expirado."),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para registrar empleados"),
            @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    })
    @PostMapping("saveRestaurantEmployee")
    public ResponseEntity<Map<String, String>> saveEmployeeByRestaurant(@RequestBody RestaurantEmployeeRequestDto restaurantEmployeeRequestDto) {
        restaurantEmployeHandler.saveRestaurantEmployee(restaurantEmployeeRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap(Constants.MESSAGE, Constants.RESTAURANT_CREATED));
    }

    @Operation(summary = "Listar pedidos por estado",
            description = "Devuelve una lista paginada de pedidos según el estado",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Estado inválido o parámetros de paginación incorrectos"),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida o token inválido/expirado."),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para consultar pedidos")
    })
    @GetMapping
    public ResponseEntity<PageResponse<OrderResponseDto>> listOrderByStatus(
            @RequestParam("status") OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10", name = "size") int size
    ) {
        var resp = employeeOrderHandler.listByStatus(status, page, size);
        return ResponseEntity.ok(resp);
    }


    @Operation(summary = "Asignarse un pedido",
            description = "Permite al empleado asignarse un pedido pendiente para prepararlo",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido asignado"),
            @ApiResponse(responseCode = "400", description = "El pedido no está disponible para asignar"),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida o token inválido/expirado."),
            @ApiResponse(responseCode = "403", description = "No puedes asignarte pedidos de este restaurante"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @PatchMapping("/{orderId}/assign")
    public ResponseEntity<OrderResponseDto> assignSelfOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(employeeOrderHandler.assignSelf(orderId));
    }

    @Operation(summary = "Marcar pedido como listo",
            description = "Cambia el estado de un pedido a LISTO",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido marcado como listo"),
            @ApiResponse(responseCode = "400", description = "El pedido no está en estado válido para marcarlo como listo"),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida o token inválido/expirado."),
            @ApiResponse(responseCode = "403", description = "No puedes actualizar pedidos de este restaurante"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @PatchMapping("/{id}/ready")
    public ResponseEntity<OrderResponseDto> markReady(@PathVariable Long id) {
        OrderResponseDto dto = employeeOrderHandler.markOrderAsReady(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Entregar pedido",
            description = "Entrega un pedido al cliente validando el PIN",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido entregado"),
            @ApiResponse(responseCode = "400", description = "El PIN es inválido o el pedido no puede entregarse"),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida o token inválido/expirado."),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para entregar pedidos de este restaurante"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @PatchMapping("/{id}/deliver")
    public ResponseEntity<OrderResponseDto> deliver(
            @PathVariable Long id,
            @RequestBody DeliverOrderRequestDto body
    ) {
        OrderResponseDto dto = employeeOrderHandler.deliver(id, body.getPin());
        return ResponseEntity.ok(dto);
    }

}
