package com.plazoleta.microservicio_plazoleta.domain.api;

import com.plazoleta.microservicio_plazoleta.domain.model.Order;
import com.plazoleta.microservicio_plazoleta.domain.model.Tracelog;

import java.util.List;

public interface IOrderServicePort {
    Order saveOrder(Order order);
    Order cancelOrder(Long orderId);
    List<Tracelog> getMyOrderTrace(Long orderId);
}
