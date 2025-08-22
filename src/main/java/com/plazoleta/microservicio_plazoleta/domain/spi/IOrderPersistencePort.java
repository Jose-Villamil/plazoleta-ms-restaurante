package com.plazoleta.microservicio_plazoleta.domain.spi;

import com.plazoleta.microservicio_plazoleta.domain.model.Order;

public interface IOrderPersistencePort {
    Order save(Order order);
    boolean clientHasOpenOrder(Long clientId);
}
