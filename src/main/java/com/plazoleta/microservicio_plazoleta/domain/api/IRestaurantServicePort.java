package com.plazoleta.microservicio_plazoleta.domain.api;

import com.plazoleta.microservicio_plazoleta.domain.model.Restaurant;
import com.plazoleta.microservicio_plazoleta.domain.util.PageResult;

public interface IRestaurantServicePort {
    void saveRestaurant(Restaurant restaurant);
    PageResult<Restaurant> listRestaurants(int page, int size);
}
