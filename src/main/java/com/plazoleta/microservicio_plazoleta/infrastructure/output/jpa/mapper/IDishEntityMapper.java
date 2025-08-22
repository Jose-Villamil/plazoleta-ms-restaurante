package com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.mapper;

import com.plazoleta.microservicio_plazoleta.domain.model.Dish;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.entity.DishEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IDishEntityMapper {
    Dish toDish(DishEntity dishEntity);
    DishEntity toEntity(Dish dish);
}

/*    // Entity -> Domain
    @Mapping(target = "categoryId", source = "category.id")
    Dish toDish(DishEntity entity);

    // Domain -> Entity
    @Mapping(target = "category", expression = "java(toCategoryRef(dish.getCategoryId()))")
    DishEntity toEntity(Dish dish);

    // helper
    default CategoryEntity toCategoryRef(Long id) {
        if (id == null) return null;
        CategoryEntity c = new CategoryEntity();
        c.setId(id);
        return c;
    }*/
