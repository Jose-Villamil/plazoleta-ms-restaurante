package com.plazoleta.microservicio_plazoleta.infrastructure.input.rest;

import com.plazoleta.microservicio_plazoleta.application.dto.request.DishRequestDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.DishListItemResponseDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.PageResponse;
import com.plazoleta.microservicio_plazoleta.application.handler.impl.DishHandler;
import com.plazoleta.microservicio_plazoleta.domain.model.Dish;
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

    @PostMapping("saveDish")
    public ResponseEntity<Map<String, String>> saveDish(@RequestBody DishRequestDto dishRequestDto) {
        dishHandler.saveDish(dishRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap(MESSAGE, DISH_CREATED));
    }

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

    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String,String>> setDishActive( @PathVariable Long id, @RequestParam boolean active) {
        dishHandler.setDishActive(id, active);
        String msg = active ? "Plato habilitado" : "Plato deshabilitado";
        return ResponseEntity.ok(Collections.singletonMap(MESSAGE, msg));
    }

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
