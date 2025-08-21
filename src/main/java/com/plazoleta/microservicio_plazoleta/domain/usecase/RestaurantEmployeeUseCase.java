package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.api.IRestaurantEmployeeServicePort;
import com.plazoleta.microservicio_plazoleta.domain.model.RestaurantEmployee;
import com.plazoleta.microservicio_plazoleta.domain.spi.IRestaurantEmployeePersistencePort;

public class RestaurantEmployeeUseCase implements IRestaurantEmployeeServicePort {

    private final IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort;

    public RestaurantEmployeeUseCase(IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort) {
        this.restaurantEmployeePersistencePort = restaurantEmployeePersistencePort;
    }

    @Override
    public RestaurantEmployee saveRestaurantEmployee(RestaurantEmployee restaurantEmployee) {
       return restaurantEmployeePersistencePort.saveRestaurantEmployee(restaurantEmployee);
    }
}
