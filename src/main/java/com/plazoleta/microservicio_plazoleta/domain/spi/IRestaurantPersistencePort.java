package com.plazoleta.microservicio_plazoleta.domain.spi;

import com.plazoleta.microservicio_plazoleta.domain.model.Restaurant;
import com.plazoleta.microservicio_plazoleta.domain.util.PageResult;

import java.util.Optional;

public interface IRestaurantPersistencePort {
    void saveRestaurant(Restaurant restaurant);
    Optional<Restaurant> findRestaurantById(Long id);
    PageResult<Restaurant> findAllOrderByNameAsc(int page, int size);
}
