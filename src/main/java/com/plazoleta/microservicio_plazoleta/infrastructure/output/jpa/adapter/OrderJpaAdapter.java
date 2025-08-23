package com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.adapter;

import com.plazoleta.microservicio_plazoleta.domain.model.Order;
import com.plazoleta.microservicio_plazoleta.domain.model.OrderStatus;
import com.plazoleta.microservicio_plazoleta.domain.spi.IOrderPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.util.PageResult;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.entity.OrderEntity;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.mapper.IOrderEntityMapper;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.repository.IOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class OrderJpaAdapter implements IOrderPersistencePort {

    private final IOrderRepository orderRepository;
    private final IOrderEntityMapper orderEntityMapper;

    @Override
    public Order save(Order order) {
        OrderEntity entity = orderEntityMapper.toEntity(order);
        orderEntityMapper.setItems(order, entity);
        OrderEntity saved = orderRepository.save(entity);
        Order out = orderEntityMapper.toOrder(saved);
        orderEntityMapper.setBack(saved, out);
        return out;
    }


    @Override
    public boolean existsByClientAndStatuses(Long clientId, Set<OrderStatus> statuses) {
        return orderRepository.existsByClientIdAndStatusIn(clientId, statuses);
    }

    @Override
    public PageResult<Order> findByRestaurantAndStatus(Long restaurantId, OrderStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        var result = orderRepository.findByRestaurantIdAndStatusOrderByCreatedAtDesc(restaurantId, status, pageable);
        var items = result.getContent().stream().map(e -> {
            var o = orderEntityMapper.toOrder(e);
            orderEntityMapper.setBack(e, o);
            return o;
        }).toList();
        return new PageResult<>(items, result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());

    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id).map(orderEntityMapper::toOrder);
    }


}

