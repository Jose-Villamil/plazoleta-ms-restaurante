package com.plazoleta.microservicio_plazoleta.domain.api;

import com.plazoleta.microservicio_plazoleta.domain.model.Dish;

import java.util.Optional;

public interface IDishServicePort {
    void saveDish(Dish dish);
    void updateDish(Dish dish);
}
