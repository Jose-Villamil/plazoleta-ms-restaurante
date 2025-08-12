package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.api.IRestaurantServicePort;
import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.model.Restaurant;
import com.plazoleta.microservicio_plazoleta.domain.model.User;
import com.plazoleta.microservicio_plazoleta.domain.spi.IRestaurantPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IUserPersistencePort;

import static com.plazoleta.microservicio_plazoleta.domain.util.Constantes.*;
import static com.plazoleta.microservicio_plazoleta.domain.util.DomainMessages.*;

public class RestaurantUseCase implements IRestaurantServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserPersistencePort userPersistencePort;

    public RestaurantUseCase(IRestaurantPersistencePort restaurantPersistencePort,  IUserPersistencePort userPersistencePort) {
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.userPersistencePort = userPersistencePort;
    }

    @Override
    public void saveRestaurant(Restaurant restaurant) {

        RestaurantValidator.validate(restaurant);

        User owner = userPersistencePort.findById(restaurant.getIdOwner())
                .orElseThrow(() -> new DomainException(OWNER_NOT_FOUND));

        if(!ROLE_PROPIETARIO.equalsIgnoreCase(owner.getRole().getName())){
            throw new DomainException(USER_DOESNOT_HAVE_ROL + ROLE_PROPIETARIO);
        }

        restaurantPersistencePort.saveRestaurant(restaurant);
    }
}
