package com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.repository;

import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.entity.RestaurantEmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRestaurantEmployeeRepository extends JpaRepository<RestaurantEmployeeEntity, Long> {
    Optional<RestaurantEmployeeEntity> findByEmployeeId(Long employeeId);
}
