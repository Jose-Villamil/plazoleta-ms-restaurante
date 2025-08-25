package com.plazoleta.microservicio_plazoleta.infrastructure.input.rest;

import com.plazoleta.microservicio_plazoleta.application.dto.request.RestaurantRequestDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.PageResponse;
import com.plazoleta.microservicio_plazoleta.application.dto.response.RestaurantListItemResponseDto;
import com.plazoleta.microservicio_plazoleta.application.handler.impl.RestaurantHandler;
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
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantHandler restaurantHandler;

    @Operation(summary = "Crear restaurante",
            description = "Crea un nuevo restaurante",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Creado"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida o token inválido/expirado."),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para realizar esta acción. Verifica tu rol o inicia sesión con una cuenta autorizada."),
            @ApiResponse(responseCode = "409", description = "El registro ya existe o viola una restricción de integridad.")
    })
    @PostMapping("saveRestaurant")
    public ResponseEntity<Map<String, String>> saveRestaurant( @RequestBody RestaurantRequestDto restaurant) {
        restaurantHandler.saveRestaurant(restaurant);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap(Constants.MESSAGE, Constants.RESTAURANT_CREATED));
    }

    @Operation(summary = "Listar restaurantes",
            description = "Devuelve una página de restaurantes (paginación por parámetros page y size)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida o token inválido/expirado."),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para realizar esta acción.")
    })
    @GetMapping("")
    public ResponseEntity<PageResponse<RestaurantListItemResponseDto>> list(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        var resp = restaurantHandler.list(page, size);
        return ResponseEntity.ok(resp);
    }
}
