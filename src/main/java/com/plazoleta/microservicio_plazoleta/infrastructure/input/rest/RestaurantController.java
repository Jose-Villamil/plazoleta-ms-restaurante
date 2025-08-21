package com.plazoleta.microservicio_plazoleta.infrastructure.input.rest;

import com.plazoleta.microservicio_plazoleta.application.dto.request.RestaurantRequestDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.PageResponse;
import com.plazoleta.microservicio_plazoleta.application.dto.response.RestaurantListItemResponseDto;
import com.plazoleta.microservicio_plazoleta.application.handler.impl.RestaurantHandler;
import com.plazoleta.microservicio_plazoleta.infrastructure.configuration.Constants;
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

    @PostMapping("saveRestaurant")
    public ResponseEntity<Map<String, String>> saveRestaurant( @RequestBody RestaurantRequestDto restaurant) {
        restaurantHandler.saveRestaurant(restaurant);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap(Constants.MESSAGE, Constants.RESTAURANT_CREATED));
    }

    @GetMapping("")
    public ResponseEntity<PageResponse<RestaurantListItemResponseDto>> list(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        var resp = restaurantHandler.list(page, size);
        return ResponseEntity.ok(resp);
    }
}
