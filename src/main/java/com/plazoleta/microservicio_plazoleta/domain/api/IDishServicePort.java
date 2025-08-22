package com.plazoleta.microservicio_plazoleta.domain.api;

import com.plazoleta.microservicio_plazoleta.domain.model.Dish;
import com.plazoleta.microservicio_plazoleta.domain.util.PageResult;

public interface IDishServicePort {
    void saveDish(Dish dish);
    void updateDish(Dish dish);
    void setDishActive(Long dishId, boolean active);
    PageResult<Dish> listDishesByRestaurant(Long restaurantId, Long categoryId, int page, int size);
}
