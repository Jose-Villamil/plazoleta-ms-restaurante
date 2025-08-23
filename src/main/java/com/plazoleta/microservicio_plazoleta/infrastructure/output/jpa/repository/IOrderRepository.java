package com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.repository;

import com.plazoleta.microservicio_plazoleta.domain.model.OrderStatus;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface IOrderRepository extends JpaRepository<OrderEntity, Long> {
    Page<OrderEntity> findByRestaurantIdAndStatusOrderByCreatedAtDesc(Long restaurantId, OrderStatus status, Pageable pageable);
    Optional<OrderEntity> findByClientId(Long clientId);
    boolean existsByClientIdAndStatusIn(Long clientId, Collection<OrderStatus> statuses);
}
