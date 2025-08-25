package com.plazoleta.microservicio_plazoleta.infrastructure.input.rest;

import com.plazoleta.microservicio_plazoleta.application.dto.request.DishRequestDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.DishListItemResponseDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.PageResponse;
import com.plazoleta.microservicio_plazoleta.application.handler.impl.DishHandler;
import com.plazoleta.microservicio_plazoleta.domain.model.Dish;
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

import static com.plazoleta.microservicio_plazoleta.infrastructure.configuration.Constants.*;

@RestController
@RequestMapping("/api/v1/dishes")
@RequiredArgsConstructor
public class DishController {

    private final DishHandler dishHandler;

    @Operation(summary = "Crear plato",
            description = "Crea un nuevo plato para un restaurante",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Creado"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida o token inválido/expirado."),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para realizar esta acción."),
            @ApiResponse(responseCode = "409", description = "El registro ya existe o viola una restricción de integridad.")
    })
    @PostMapping("saveDish")
    public ResponseEntity<Map<String, String>> saveDish(@RequestBody DishRequestDto dishRequestDto) {
        dishHandler.saveDish(dishRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap(MESSAGE, DISH_CREATED));
    }

    @Operation(summary = "Actualizar plato",
            description = "Actualiza precio y descripción de un plato",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Actualizado"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida o token inválido/expirado."),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para realizar esta acción."),
            @ApiResponse(responseCode = "404", description = "Plato no encontrado")
    })
    @PatchMapping("updateDish/{id}")
    public ResponseEntity<Map<String, String>> updateDish(@PathVariable Long id, @RequestBody DishRequestDto dishRequestDto) {
        Dish dishUpdate = new Dish();
        dishUpdate.setId(id);
        dishUpdate.setPrice(dishRequestDto.getPrice());
        dishUpdate.setDescription(dishRequestDto.getDescription());
        dishHandler.updateDish(dishUpdate);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap(MESSAGE, DISH_UPDATE));
    }

    @Operation(summary = "Habilitar/Deshabilitar plato",
            description = "Cambia el estado activo del plato",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida o token inválido/expirado."),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para realizar esta acción."),
            @ApiResponse(responseCode = "404", description = "Plato no encontrado")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String,String>> setDishActive( @PathVariable Long id, @RequestParam boolean active) {
        dishHandler.setDishActive(id, active);
        String msg = active ? "Plato habilitado" : "Plato deshabilitado";
        return ResponseEntity.ok(Collections.singletonMap(MESSAGE, msg));
    }

    @Operation(summary = "Listar platos por restaurante",
            description = "Devuelve una página de platos filtrados por restaurante y opcionalmente por categoría (paginación por parámetros page y size)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Parámetros de paginación/filtrado inválidos"),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida o token inválido/expirado."),
    })
    @GetMapping("listDishes/{restaurantId}")
    public ResponseEntity<PageResponse<DishListItemResponseDto>> list(
            @PathVariable Long restaurantId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10", name = "size") int size
    ) {
        var resp = dishHandler.list(restaurantId, categoryId, page, size);
        return ResponseEntity.ok(resp);
    }
}
