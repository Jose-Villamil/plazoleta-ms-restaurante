package com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.repository;

import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRestaurantRepository extends JpaRepository<RestaurantEntity, Long> {
}
