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
import static com.plazoleta.microservicio_plazoleta.domain.util.Constantes.ROLE_PROPIETARIO;
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

        Long ownerId = authServicePort.getAuthenticatedUserId();
        User owner = userPersistencePort.findById(ownerId)
                .orElseThrow(() -> new DomainException(OWNER_NOT_FOUND));
        Restaurant restaurant = restaurantPersistencePort.getRestaurantById(dish.getRestaurantId())
                .orElseThrow(() -> new DomainException(RESTAURANT_NOT_FOUND));

        if(!ROLE_PROPIETARIO.equalsIgnoreCase(owner.getRole().getName())){
            throw new DomainException(USER_DOESNOT_HAVE_ROL + ROLE_PROPIETARIO);
        }

        if(!restaurant.getIdOwner().equals(owner.getId())){
            throw new DomainException(NOT_OWNER_RESTAURANT);
        }

        dish.setActive(true);
        dishPersistencePort.saveDish(dish);
    }

    private static void validateDish(Dish dish) {
        requireNonNull(dish.getName(), String.format(FIELD_REQUIRED, "Nombre"));
        requireNonNull(dish.getDescription(), String.format(FIELD_REQUIRED, "Descripción"));
        requireNonNull(dish.getUrlImage(), String.format(FIELD_REQUIRED, "Url Imagen"));
        requireNonBlack(dish.getCategoryId(), String.format(FIELD_REQUIRED, "Categoría"));
        requireNonBlack(dish.getRestaurantId(), String.format(FIELD_REQUIRED, "Restaurante"));
        validatePositiveNumberInt(dish.getPrice(), String.format(FIELD_INVALID, "Precio"));
    }
}
