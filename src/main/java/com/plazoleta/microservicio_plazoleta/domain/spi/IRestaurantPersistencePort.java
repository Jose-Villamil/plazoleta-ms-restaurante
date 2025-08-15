package com.plazoleta.microservicio_plazoleta.domain.spi;

import com.plazoleta.microservicio_plazoleta.domain.model.Restaurant;

import java.util.Optional;

public interface IRestaurantPersistencePort {
    void saveRestaurant(Restaurant restaurant);
    Optional<Restaurant> getRestaurantById(Long id);
}
