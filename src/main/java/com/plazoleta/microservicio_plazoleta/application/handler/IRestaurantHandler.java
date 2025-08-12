package com.plazoleta.microservicio_plazoleta.application.handler;

import com.plazoleta.microservicio_plazoleta.application.dto.request.RestaurantRequestDto;


public interface IRestaurantHandler {
    void saveRestaurant(RestaurantRequestDto restaurantRequestDto);
}
