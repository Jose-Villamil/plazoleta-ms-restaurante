package com.plazoleta.microservicio_plazoleta.application.mapper;

import com.plazoleta.microservicio_plazoleta.application.dto.response.PageResponse;
import com.plazoleta.microservicio_plazoleta.application.dto.response.RestaurantListItemResponseDto;
import com.plazoleta.microservicio_plazoleta.domain.model.Restaurant;
import com.plazoleta.microservicio_plazoleta.domain.util.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IRestaurantListMapper {
    RestaurantListItemResponseDto toRestaurant(Restaurant restaurant);

    default PageResponse<RestaurantListItemResponseDto> toPageResponse(PageResult<Restaurant> pr) {
        var items = pr.getItems().stream().map(this::toRestaurant).toList();
        return new PageResponse<>(items, pr.getPage(), pr.getSize(), pr.getTotalElements(), pr.getTotalPages());
    }
}
