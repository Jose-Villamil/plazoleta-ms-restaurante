package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.api.IAuthServicePort;
import com.plazoleta.microservicio_plazoleta.domain.api.IRestaurantEmployeeServicePort;
import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.model.Restaurant;
import com.plazoleta.microservicio_plazoleta.domain.model.RestaurantEmployee;
import com.plazoleta.microservicio_plazoleta.domain.spi.IRestaurantEmployeePersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IRestaurantPersistencePort;

import static com.plazoleta.microservicio_plazoleta.domain.util.DomainMessages.*;
import static com.plazoleta.microservicio_plazoleta.domain.util.DomainMessages.NOT_OWNER_RESTAURANT;

public class RestaurantEmployeeUseCase implements IRestaurantEmployeeServicePort {

    private final IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort;
    private final IRestaurantPersistencePort  restaurantPersistencePort;
    private final IAuthServicePort  authServicePort;

    public RestaurantEmployeeUseCase(IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort , IRestaurantPersistencePort restaurantPersistencePort,  IAuthServicePort authServicePort) {
        this.restaurantEmployeePersistencePort = restaurantEmployeePersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.authServicePort = authServicePort;
    }

    @Override
    public RestaurantEmployee saveRestaurantEmployee(RestaurantEmployee restaurantEmployee) {
        validateOwnerAndRestaurant(restaurantEmployee.getRestaurantId());
       return restaurantEmployeePersistencePort.saveRestaurantEmployee(restaurantEmployee);
    }

    private void validateOwnerAndRestaurant(Long restaurantId) {
        Long ownerId = authServicePort.getAuthenticatedUserId();

        Restaurant restaurant = restaurantPersistencePort.findRestaurantById(restaurantId)
                .orElseThrow(() -> new DomainException(RESTAURANT_NOT_FOUND));

        if(!restaurant.getIdOwner().equals(ownerId)){
            throw new DomainException(NOT_OWNER_RESTAURANT);
        }
    }
}
