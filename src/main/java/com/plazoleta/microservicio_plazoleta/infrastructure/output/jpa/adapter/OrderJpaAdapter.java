package com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.adapter;


import com.plazoleta.microservicio_plazoleta.domain.model.Order;
import com.plazoleta.microservicio_plazoleta.domain.model.OrderStatus;
import com.plazoleta.microservicio_plazoleta.domain.spi.IOrderPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.util.Constantes;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.entity.OrderEntity;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.mapper.IOrderEntityMapper;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.repository.IOrderRepository;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;

@RequiredArgsConstructor
public class OrderJpaAdapter implements IOrderPersistencePort {

    private final IOrderRepository repository;
    private final IOrderEntityMapper mapper;

    @Override
    public Order save(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        mapper.setItems(order, entity);
        OrderEntity saved = repository.save(entity);
        Order out = mapper.toOrder(saved);
        mapper.setBack(saved, out);
        return out;
    }

    @Override
    public boolean clientHasOpenOrder(Long clientId) {
        return repository.existsByClientIdAndStatusIn(
                clientId, EnumSet.of(OrderStatus.PENDIENTE, OrderStatus.EN_PREPARACION, OrderStatus.LISTO)
        );
    }
}

