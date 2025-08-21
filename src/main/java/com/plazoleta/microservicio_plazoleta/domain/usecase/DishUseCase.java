package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.api.IAuthServicePort;
import com.plazoleta.microservicio_plazoleta.domain.api.IDishServicePort;
import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.model.Dish;
import com.plazoleta.microservicio_plazoleta.domain.model.Restaurant;
import com.plazoleta.microservicio_plazoleta.domain.model.User;
import com.plazoleta.microservicio_plazoleta.domain.spi.IDishPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IRestaurantPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IUserPersistencePort;

import static com.plazoleta.microservicio_plazoleta.domain.usecase.ValidatorUseCase.*;
import static com.plazoleta.microservicio_plazoleta.domain.util.Constantes.ROLE_OWNER;
import static com.plazoleta.microservicio_plazoleta.domain.util.DomainMessages.*;

public class DishUseCase implements IDishServicePort {

    private final IDishPersistencePort dishPersistencePort;
    private final IAuthServicePort authServicePort;
    private final IUserPersistencePort userPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;

    public DishUseCase(IDishPersistencePort dishPersistencePort, IAuthServicePort authServicePort, IUserPersistencePort userPersistencePort, IRestaurantPersistencePort restaurantPersistencePort) {
        this.dishPersistencePort = dishPersistencePort;
        this.authServicePort = authServicePort;
        this.userPersistencePort = userPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
    }

    @Override
    public void saveDish(Dish dish) {
        validateDish(dish);
        validateOwnerAndRestaurant(dish.getRestaurantId());
        dish.setActive(true);
        dishPersistencePort.saveDish(dish);
    }

    @Override
    public void updateDish(Dish dish) {
        requireNonNull(dish.getDescription(), String.format(FIELD_REQUIRED, "Descripción"));
        validatePositiveNumberInt(dish.getPrice(), String.format(FIELD_INVALID, "Precio"));

        Dish dishDb = dishPersistencePort.findDishById(dish.getId())
                .orElseThrow(()-> new DomainException(DISH_NOT_FOUND));

        validateOwnerAndRestaurant(dishDb.getRestaurantId());
        dishDb.setPrice(dish.getPrice());
        dishDb.setDescription(dish.getDescription());
        dishPersistencePort.updateDish(dishDb);
    }

    @Override
    public void setDishActive(Long dishId, boolean active) {

        if (dishId == null || dishId <= 0) {
            throw new DomainException("El id del plato es obligatorio");
        }

        Dish dishDb = dishPersistencePort.findDishById(dishId)
                .orElseThrow(() -> new DomainException(DISH_NOT_FOUND));
        validateOwnerAndRestaurant(dishDb.getRestaurantId());
        dishDb.setActive(active);
        dishPersistencePort.updateDish(dishDb);
    }

    private void validateOwnerAndRestaurant(Long restaurantId) {
        Long ownerId = authServicePort.getAuthenticatedUserId();
        User owner = userPersistencePort.findById(ownerId)
                .orElseThrow(() -> new DomainException(OWNER_NOT_FOUND));

        if(!ROLE_OWNER.equalsIgnoreCase(owner.getRole().getName())){
            throw new DomainException(String.format(USER_DOESNOT_HAVE_ROL, owner.getRole().getName()));
        }
        Restaurant restaurant = restaurantPersistencePort.findRestaurantById(restaurantId)
                .orElseThrow(() -> new DomainException(RESTAURANT_NOT_FOUND));

        if(!restaurant.getIdOwner().equals(owner.getId())){
            throw new DomainException(NOT_OWNER_RESTAURANT);
        }
    }
}
