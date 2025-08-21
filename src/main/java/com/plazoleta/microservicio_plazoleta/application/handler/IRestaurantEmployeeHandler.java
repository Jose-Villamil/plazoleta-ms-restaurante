package com.plazoleta.microservicio_plazoleta.application.handler;

import com.plazoleta.microservicio_plazoleta.application.dto.request.RestaurantEmployeeRequestDto;

public interface IRestaurantEmployeeHandler {
    void saveRestaurantEmployee(RestaurantEmployeeRequestDto restaurantEmployeeRequestDto);
}
