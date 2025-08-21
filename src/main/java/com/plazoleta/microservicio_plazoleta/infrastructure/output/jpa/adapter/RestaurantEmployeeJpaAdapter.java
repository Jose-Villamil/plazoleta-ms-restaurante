package com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.adapter;

import com.plazoleta.microservicio_plazoleta.domain.model.RestaurantEmployee;
import com.plazoleta.microservicio_plazoleta.domain.spi.IRestaurantEmployeePersistencePort;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.entity.RestaurantEmployeeEntity;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.mapper.IRestaurantEntityEmployeeMapper;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.repository.IRestaurantEmployeeRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestaurantEmployeeJpaAdapter implements IRestaurantEmployeePersistencePort {
    private final IRestaurantEmployeeRepository restaurantEmployeeRepository;
    private final IRestaurantEntityEmployeeMapper restaurantEmployeeMapper;

    @Override
    public RestaurantEmployee saveRestaurantEmployee(RestaurantEmployee restaurantEmployee) {
        RestaurantEmployeeEntity entity = restaurantEmployeeRepository.save(restaurantEmployeeMapper.toEntity(restaurantEmployee));
        return restaurantEmployeeMapper.toRestaurantEmployee(entity);
    }

}
