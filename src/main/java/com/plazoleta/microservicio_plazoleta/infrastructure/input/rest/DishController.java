package com.plazoleta.microservicio_plazoleta.infrastructure.input.rest;

import com.plazoleta.microservicio_plazoleta.application.dto.request.DishRequestDto;
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
@RequestMapping("/api/dishes")
@RequiredArgsConstructor
public class DishController {
    private final DishHandler dishHandler;

    @PostMapping
    public ResponseEntity<Map<String, String>> saveDish(@RequestBody DishRequestDto dishRequestDto) {
        dishHandler.saveDish(dishRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap(MESSAGE, DISH_CREATED));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateDish(@PathVariable Long id, @RequestBody DishRequestDto dishRequestDto) {
        Dish dishUpdate = new Dish();
        dishUpdate.setId(id);
        dishUpdate.setPrice(dishRequestDto.getPrice());
        dishUpdate.setDescription(dishRequestDto.getDescription());
        dishHandler.updateDish(dishUpdate);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap(MESSAGE, DISH_UPDATE));
    }
}
