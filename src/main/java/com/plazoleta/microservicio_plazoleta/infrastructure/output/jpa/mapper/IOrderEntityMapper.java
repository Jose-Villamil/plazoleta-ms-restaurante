package com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.mapper;

import com.plazoleta.microservicio_plazoleta.domain.model.Order;
import com.plazoleta.microservicio_plazoleta.domain.model.OrderItem;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.entity.OrderEntity;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.entity.OrderItemEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IOrderEntityMapper {

    @Mapping(target = "items", ignore = true)
    OrderEntity toEntity(Order order);

    Order toOrder(OrderEntity entity);

    default void setItems(Order order, OrderEntity entity) {
        List<OrderItemEntity> items = order.getItems().stream().map(oi -> {
            OrderItemEntity e = new OrderItemEntity();
            e.setDishId(oi.getDishId());
            e.setQuantity(oi.getQuantity());
            e.setOrder(entity);
            return e;
        }).toList();
        entity.setItems(items);
    }

    default void setBack(OrderEntity entity, Order order) {
        List<OrderItem> items = entity.getItems().stream().map(e -> {
            OrderItem oi = new OrderItem();
            oi.setDishId(e.getDishId());
            oi.setQuantity(e.getQuantity());
            return oi;
        }).toList();
        order.setItems(items);
    }
}

