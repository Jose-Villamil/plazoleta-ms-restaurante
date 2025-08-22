package com.plazoleta.microservicio_plazoleta.application.handler.impl;

import com.plazoleta.microservicio_plazoleta.application.dto.request.DishRequestDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.DishListItemResponseDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.PageResponse;
import com.plazoleta.microservicio_plazoleta.application.handler.IDishHandler;
import com.plazoleta.microservicio_plazoleta.application.mapper.IDishListMapper;
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
    private final IDishListMapper dishListMapper;

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

    @Override
    public PageResponse<DishListItemResponseDto> list(Long restaurantId, Long categoryId, int page, int size) {
        return dishListMapper.toPageResponse(dishServicePort.listDishesByRestaurant(restaurantId, categoryId, page, size));
    }

}
