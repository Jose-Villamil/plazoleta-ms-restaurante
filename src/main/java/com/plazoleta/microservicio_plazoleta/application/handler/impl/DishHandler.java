package com.plazoleta.microservicio_plazoleta.application.handler.impl;

import com.plazoleta.microservicio_plazoleta.application.dto.request.DishRequestDto;
import com.plazoleta.microservicio_plazoleta.application.handler.IDishHandler;
import com.plazoleta.microservicio_plazoleta.application.mapper.IDishMapper;
import com.plazoleta.microservicio_plazoleta.domain.api.IDishServicePort;
import com.plazoleta.microservicio_plazoleta.domain.model.Dish;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DishHandler implements IDishHandler {
    private final IDishServicePort dishServicePort;
    private final IDishMapper dishMapper;

    @Override
    public void saveDish(DishRequestDto dishRequestDto) {
        dishServicePort.saveDish(dishMapper.toDish(dishRequestDto));
    }

    @Override
    public void updateDish(Dish dishUpdate) {
        dishServicePort.updateDish(dishUpdate);
    }

    @Override
    public void setDishActive(Long dishId, boolean active) {
        dishServicePort.setDishActive(dishId, active);
    }

}
