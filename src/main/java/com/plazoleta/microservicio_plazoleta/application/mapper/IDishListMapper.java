package com.plazoleta.microservicio_plazoleta.application.mapper;

import com.plazoleta.microservicio_plazoleta.application.dto.response.DishListItemResponseDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.PageResponse;
import com.plazoleta.microservicio_plazoleta.domain.model.Dish;
import com.plazoleta.microservicio_plazoleta.domain.util.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IDishListMapper {
    DishListItemResponseDto toDish(Dish dish);

    default PageResponse<DishListItemResponseDto> toPageResponse(PageResult<Dish> page) {
        var items = page.getItems().stream().map(this::toDish).toList();
        return new PageResponse<>(items, page.getPage(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

}
