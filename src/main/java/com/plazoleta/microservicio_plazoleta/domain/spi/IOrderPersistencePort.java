package com.plazoleta.microservicio_plazoleta.domain.spi;

import com.plazoleta.microservicio_plazoleta.domain.model.Order;
import com.plazoleta.microservicio_plazoleta.domain.model.OrderStatus;
import com.plazoleta.microservicio_plazoleta.domain.util.PageResult;

import java.util.Optional;
public interface IOrderPersistencePort {
    Order save(Order order);
    boolean clientHasOpenOrder(Long clientId);
    PageResult<Order> findByRestaurantAndStatus(Long restaurantId, OrderStatus status, int page, int size);
    Optional<Order> findById(Long id);
}
