package com.plazoleta.microservicio_plazoleta.domain.spi;

import com.plazoleta.microservicio_plazoleta.domain.model.Dish;

import java.util.Optional;

public interface IDishPersistencePort {
    void saveDish(Dish dish);
    void updateDish(Dish dish);
    Optional<Dish> findDishById(Long dishId);
}
