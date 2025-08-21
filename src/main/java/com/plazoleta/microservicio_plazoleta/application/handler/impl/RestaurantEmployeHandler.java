package com.plazoleta.microservicio_plazoleta.application.handler.impl;

import com.plazoleta.microservicio_plazoleta.application.dto.request.RestaurantEmployeeRequestDto;
import com.plazoleta.microservicio_plazoleta.application.handler.IRestaurantEmployeeHandler;
import com.plazoleta.microservicio_plazoleta.application.mapper.IRestaurantEmployeeMapper;
import com.plazoleta.microservicio_plazoleta.domain.api.IRestaurantEmployeeServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantEmployeHandler implements IRestaurantEmployeeHandler {

    private final IRestaurantEmployeeServicePort restaurantEmployeeServicePort;
    private final IRestaurantEmployeeMapper  restaurantEmployeeMapper;

    @Override
    public void saveRestaurantEmployee(RestaurantEmployeeRequestDto restaurantEmployeeRequestDto) {
        restaurantEmployeeServicePort.saveRestaurantEmployee(restaurantEmployeeMapper.toRestaurantEmployee(restaurantEmployeeRequestDto));
    }
}
