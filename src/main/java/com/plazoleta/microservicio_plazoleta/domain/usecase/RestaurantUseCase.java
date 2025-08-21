package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.api.IRestaurantServicePort;
import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.model.Restaurant;
import com.plazoleta.microservicio_plazoleta.domain.model.User;
import com.plazoleta.microservicio_plazoleta.domain.spi.IRestaurantPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IUserPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.util.PageResult;

import static com.plazoleta.microservicio_plazoleta.domain.usecase.ValidatorUseCase.*;
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

        validateRestaurant(restaurant);
        User owner = userPersistencePort.findById(restaurant.getIdOwner())
                .orElseThrow(() -> new DomainException(OWNER_NOT_FOUND));
        if(!ROLE_OWNER.equalsIgnoreCase(owner.getRole().getName())){
            throw new DomainException(String.format(USER_DOESNOT_HAVE_ROL, owner.getRole().getName()));
        }

        restaurantPersistencePort.saveRestaurant(restaurant);
    }

    @Override
    public PageResult<Restaurant> listRestaurants(int page, int size) {
        if (size <= 0) size = 10;
        if (page < 0) page = 0;
        return restaurantPersistencePort.findAllOrderByNameAsc(page, size);
    }

}
