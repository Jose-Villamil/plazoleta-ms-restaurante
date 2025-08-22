package com.plazoleta.microservicio_plazoleta.domain.spi;

import com.plazoleta.microservicio_plazoleta.domain.model.RestaurantEmployee;

import java.util.Optional;

public interface IRestaurantEmployeePersistencePort {
    RestaurantEmployee saveRestaurantEmployee(RestaurantEmployee restaurantEmployeeModel);
    Optional<Long> findRestaurantIdByEmployeeId(Long employeeId);
}
