package com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.repository;

import com.plazoleta.microservicio_plazoleta.domain.model.OrderStatus;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOrderRepository extends JpaRepository<OrderEntity, Long> {
    boolean existsByClientIdAndStatusIn(Long clientId, Iterable<OrderStatus> statuses);
    Page<OrderEntity> findByRestaurantIdAndStatusOrderByCreatedAtDesc(Long restaurantId, OrderStatus status, Pageable pageable);
}
