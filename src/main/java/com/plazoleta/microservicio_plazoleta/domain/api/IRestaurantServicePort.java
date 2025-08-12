package com.plazoleta.microservicio_plazoleta.domain.api;

import com.plazoleta.microservicio_plazoleta.domain.model.Restaurant;

public interface IRestaurantServicePort {
    void saveRestaurant(Restaurant restaurant);
}
