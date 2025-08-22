package com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.repository;

import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.entity.DishEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDishRepository extends JpaRepository<DishEntity, Long> {
    Page<DishEntity> findByRestaurantIdAndActiveTrue(Long restaurantId, Pageable pageable);
    Page<DishEntity> findByRestaurantIdAndCategoryIdAndActiveTrue(Long restaurantId, Long categoryId, Pageable pageable);
}
