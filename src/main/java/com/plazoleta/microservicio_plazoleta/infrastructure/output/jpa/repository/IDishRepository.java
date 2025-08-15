package com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.repository;

import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.entity.DishEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDishRepository extends JpaRepository<DishEntity, Long> {
}
