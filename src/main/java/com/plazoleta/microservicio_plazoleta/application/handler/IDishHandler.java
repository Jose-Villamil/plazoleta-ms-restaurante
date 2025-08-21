package com.plazoleta.microservicio_plazoleta.application.handler;

import com.plazoleta.microservicio_plazoleta.application.dto.request.DishRequestDto;
import com.plazoleta.microservicio_plazoleta.domain.model.Dish;

public interface IDishHandler {
    void saveDish(DishRequestDto dishRequestDto);
    void updateDish(Dish dishUpdate);
    void setDishActive(Long dishId, boolean active);

}
