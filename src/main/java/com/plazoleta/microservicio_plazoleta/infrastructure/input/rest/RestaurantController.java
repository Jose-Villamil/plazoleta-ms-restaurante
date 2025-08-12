package com.plazoleta.microservicio_plazoleta.infrastructure.input.rest;

import com.plazoleta.microservicio_plazoleta.application.dto.request.RestaurantRequestDto;
import com.plazoleta.microservicio_plazoleta.application.handler.impl.RestaurantHandler;
import com.plazoleta.microservicio_plazoleta.infrastructure.configuration.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantHandler restaurantHandler;

    @PostMapping
    public ResponseEntity<Map<String, String>> saveRestaurant( @RequestBody RestaurantRequestDto restaurant) {
        restaurantHandler.saveRestaurant(restaurant);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap(Constants.MESSAGE, Constants.RESTAURANT_CREATED));
    }
}
