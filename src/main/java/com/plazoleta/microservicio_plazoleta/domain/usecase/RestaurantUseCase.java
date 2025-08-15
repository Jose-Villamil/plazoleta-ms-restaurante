package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.api.IRestaurantServicePort;
import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.model.Restaurant;
import com.plazoleta.microservicio_plazoleta.domain.model.User;
import com.plazoleta.microservicio_plazoleta.domain.spi.IRestaurantPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IUserPersistencePort;

import static com.plazoleta.microservicio_plazoleta.domain.usecase.ValidatorUseCase.*;
import static com.plazoleta.microservicio_plazoleta.domain.util.Constantes.*;
import static com.plazoleta.microservicio_plazoleta.domain.util.DomainMessages.*;
import static com.plazoleta.microservicio_plazoleta.domain.util.ValidationPatterns.NUMERIC_PATTERN;
import static com.plazoleta.microservicio_plazoleta.domain.util.ValidationPatterns.PHONE_PATTERN;

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

    public static void validateRestaurant(Restaurant restaurant) {
        requireNonNull(restaurant.getName(), String.format(FIELD_REQUIRED,"Nombre"));
        requireNonNull(restaurant.getNit(), String.format(FIELD_REQUIRED,"Nit"));
        requireNonNull(restaurant.getAddress(),  String.format(FIELD_REQUIRED,"Dirección"));
        requireNonNull(restaurant.getPhone(), String.format(FIELD_REQUIRED,"Teléfono"));
        requireNonNull(restaurant.getUrlLogo(),  String.format(FIELD_REQUIRED,"Url Logo"));
        requireNonBlack(restaurant.getIdOwner(), String.format(FIELD_REQUIRED,"Id Propietario"));
        validatePattern(restaurant.getNit(), NUMERIC_PATTERN, String.format(FIELD_INVALID,"Nit"));
        validatePattern(restaurant.getPhone(), PHONE_PATTERN, String.format(FIELD_INVALID,"Teléfono"));
        validateNameNotOnlyNumbers(restaurant.getName());
    }
}
