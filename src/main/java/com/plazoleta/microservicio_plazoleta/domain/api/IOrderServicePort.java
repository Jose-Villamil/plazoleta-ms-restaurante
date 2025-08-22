package com.plazoleta.microservicio_plazoleta.domain.api;

import com.plazoleta.microservicio_plazoleta.domain.model.Order;

public interface IOrderServicePort {
    Order saveOrder(Order order);
}
