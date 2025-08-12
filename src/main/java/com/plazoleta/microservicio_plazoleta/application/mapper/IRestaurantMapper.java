package com.plazoleta.microservicio_plazoleta.application.mapper;

import com.plazoleta.microservicio_plazoleta.application.dto.request.RestaurantRequestDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.RestaurantResponseDto;
import com.plazoleta.microservicio_plazoleta.domain.model.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IRestaurantMapper {
    Restaurant toRestaurant(RestaurantRequestDto request);
    RestaurantResponseDto toResponse(Restaurant restaurant);
}
