package com.plazoleta.microservicio_plazoleta.infrastructure.input.rest;

import com.plazoleta.microservicio_plazoleta.application.dto.request.RestaurantEmployeeRequestDto;
import com.plazoleta.microservicio_plazoleta.application.handler.impl.RestaurantEmployeHandler;
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
@RequestMapping("/api/v1/restaurantEmployee")
@RequiredArgsConstructor
public class RestaurantEmployeeController {
    private final RestaurantEmployeHandler restaurantEmployeHandler;

    @PostMapping("saveRestaurantEmployee")
    public ResponseEntity<Map<String, String>> saveRestaurant(@RequestBody RestaurantEmployeeRequestDto restaurantEmployeeRequestDto) {
        restaurantEmployeHandler.saveRestaurantEmployee(restaurantEmployeeRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap(Constants.MESSAGE, Constants.RESTAURANT_CREATED));
    }
}
