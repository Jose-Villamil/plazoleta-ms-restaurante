package com.plazoleta.microservicio_plazoleta.domain.api;

import com.plazoleta.microservicio_plazoleta.domain.model.Dish;

public interface IDishServicePort {
    void saveDish(Dish dish);
    void updateDish(Dish dish);
    void setDishActive(Long dishId, boolean active);
}
