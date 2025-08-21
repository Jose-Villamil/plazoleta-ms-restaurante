package com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.mapper;

import com.plazoleta.microservicio_plazoleta.domain.model.RestaurantEmployee;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.entity.RestaurantEmployeeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IRestaurantEntityEmployeeMapper {
    RestaurantEmployee toRestaurantEmployee(RestaurantEmployeeEntity restaurantEmployeeEntity);
    RestaurantEmployeeEntity toEntity(RestaurantEmployee restaurantEmployee);
}
