package com.plazoleta.microservicio_plazoleta.application.mapper;

import com.plazoleta.microservicio_plazoleta.application.dto.request.DishRequestDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.DishResponseDto;
import com.plazoleta.microservicio_plazoleta.domain.model.Dish;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IDishMapper {
    Dish toDish(DishRequestDto dishRequestDto);
    DishResponseDto toDishResponse(Dish dish);
}

/*    @Mapping(target = "categoryId", source = "categoryId")
    Dish toDish(DishRequestDto dishRequestDto);

    @Mapping(target = "category.id", source = "categoryId")
    DishListItemResponseDto toItem(Dish dish);
    default PageResponse<DishListItemResponseDto> toPage(PageResult<Dish> page) {
        var items = page.getItems().stream().map(this::toItem).toList();
        return new PageResponse<>(items, page.getPage(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }*/
