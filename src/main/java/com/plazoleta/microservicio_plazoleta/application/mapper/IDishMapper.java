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
