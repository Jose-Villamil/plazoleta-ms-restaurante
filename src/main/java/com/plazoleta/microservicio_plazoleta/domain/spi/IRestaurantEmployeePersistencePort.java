package com.plazoleta.microservicio_plazoleta.domain.spi;

import com.plazoleta.microservicio_plazoleta.domain.model.RestaurantEmployee;

public interface IRestaurantEmployeePersistencePort {
    RestaurantEmployee saveRestaurantEmployee(RestaurantEmployee restaurantEmployeeModel);
}
