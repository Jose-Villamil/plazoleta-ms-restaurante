package com.plazoleta.microservicio_plazoleta.domain.api;

import com.plazoleta.microservicio_plazoleta.domain.model.Order;
import com.plazoleta.microservicio_plazoleta.domain.model.OrderStatus;
import com.plazoleta.microservicio_plazoleta.domain.util.PageResult;

public interface IEmployeeOrderServicePort {
    PageResult<Order> listOrdersByStatus(OrderStatus status, int page, int size);
    Order assignSelfToOrder(Long orderId);
    Order markOrderAsReady(Long orderId);
    Order deliverOrder(Long orderId, String pin);
}
