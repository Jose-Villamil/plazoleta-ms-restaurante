package com.plazoleta.microservicio_plazoleta.domain.api;

import com.plazoleta.microservicio_plazoleta.domain.model.RestaurantEmployee;

public interface IRestaurantEmployeeServicePort {
    RestaurantEmployee saveRestaurantEmployee(RestaurantEmployee restaurantEmployee);
}
