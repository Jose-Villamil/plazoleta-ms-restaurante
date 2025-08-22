package com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.mapper;

import com.plazoleta.microservicio_plazoleta.domain.model.Category;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ICategoryEntityMapper {
    Category toCategory(CategoryEntity entity);
    CategoryEntity toEntity(Category category);
}
