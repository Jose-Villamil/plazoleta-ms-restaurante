package com.plazoleta.microservicio_plazoleta.application.handler.impl;

import com.plazoleta.microservicio_plazoleta.application.dto.request.RestaurantRequestDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.PageResponse;
import com.plazoleta.microservicio_plazoleta.application.dto.response.RestaurantListItemResponseDto;
import com.plazoleta.microservicio_plazoleta.application.handler.IRestaurantHandler;
import com.plazoleta.microservicio_plazoleta.application.mapper.IRestaurantListMapper;
import com.plazoleta.microservicio_plazoleta.application.mapper.IRestaurantMapper;
import com.plazoleta.microservicio_plazoleta.domain.api.IRestaurantServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantHandler implements IRestaurantHandler {

    private final IRestaurantServicePort restaurantServicePort;
    private final IRestaurantMapper restaurantMapper;
    private final IRestaurantListMapper  restaurantListMapper;

    @Override
    public void saveRestaurant(RestaurantRequestDto request) {
        restaurantServicePort.saveRestaurant(restaurantMapper.toRestaurant(request));
    }

    @Override
    public PageResponse<RestaurantListItemResponseDto> list(int page, int size) {
        return restaurantListMapper.toPageResponse(restaurantServicePort.listRestaurants(page, size));
    }
}
