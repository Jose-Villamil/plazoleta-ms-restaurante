package com.plazoleta.microservicio_plazoleta.application.mapper;

import com.plazoleta.microservicio_plazoleta.application.dto.request.RestaurantEmployeeRequestDto;
import com.plazoleta.microservicio_plazoleta.domain.model.RestaurantEmployee;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IRestaurantEmployeeMapper {
    RestaurantEmployee toRestaurantEmployee(RestaurantEmployeeRequestDto restaurantEmployeeRequestDto);
}
