package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.api.IAuthServicePort;
import com.plazoleta.microservicio_plazoleta.domain.api.IOrderServicePort;
import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.model.*;
import com.plazoleta.microservicio_plazoleta.domain.spi.IDishPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IOrderPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IRestaurantPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.ITraceLogOutPort;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import static com.plazoleta.microservicio_plazoleta.domain.util.DomainMessages.*;

public class OrderUseCase implements IOrderServicePort {
    private final Logger log = Logger.getLogger(OrderUseCase.class.getName());

    private final IOrderPersistencePort orderPersistencePort;
    private final IAuthServicePort authServicePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IDishPersistencePort dishPersistencePort;
    private final ITraceLogOutPort  traceLogOutPort;

    private static final Set<OrderStatus> IN_PROGRESS = Set.of(
            OrderStatus.PENDIENTE,
            OrderStatus.EN_PREPARACION,
            OrderStatus.LISTO
    );

    public OrderUseCase(
            IOrderPersistencePort orderPort,
            IAuthServicePort authPort,
            IRestaurantPersistencePort restaurantPort,
            IDishPersistencePort dishPort,
            ITraceLogOutPort traceLogOutPort) {
        this.orderPersistencePort = orderPort;
        this.authServicePort = authPort;
        this.restaurantPersistencePort = restaurantPort;
        this.dishPersistencePort = dishPort;
        this.traceLogOutPort = traceLogOutPort;
    }

    @Override
    @Transactional
    public Order saveOrder(Order order) {
        final Long clientId = authServicePort.getAuthenticatedUserId();

        if (order == null || order.getRestaurantId() == null) {
            throw new DomainException(String.format(FIELD_REQUIRED, "Restaurante"));
        }
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new DomainException(ORDER_MUST_HAVE_AT_LEAST_ONE_DISH);
        }

        if (orderPersistencePort.existsByClientAndStatuses(clientId, IN_PROGRESS)) {
            throw new DomainException(ORDER_ALREADY_IN_PROGRESS);
        }

        Restaurant restaurant = restaurantPersistencePort.findRestaurantById(order.getRestaurantId())
                .orElseThrow(() -> new DomainException(RESTAURANT_NOT_FOUND));

        var seenDishes = new HashSet<Long>();
        for (OrderItem it : order.getItems()) {
            if (it.getDishId() == null || it.getQuantity() <= 0) {
                throw new DomainException(ORDER_ITEM_INVALID_QUANTITY);
            }
            if (!seenDishes.add(it.getDishId())) {
                throw new DomainException(ORDER_DUPLICATE_DISHES_NOT_ALLOWED);
            }

            Dish dish = dishPersistencePort.findDishById(it.getDishId())
                    .orElseThrow(() -> new DomainException(DISH_NOT_FOUND));

            if (!dish.isActive()) {
                throw new DomainException(ORDER_DISH_NOT_ACTIVE);
            }
            if (!order.getRestaurantId().equals(dish.getRestaurantId())) {
                throw new DomainException(ORDER_ALL_DISHES_SAME_RESTAURANT);
            }
        }

        String clientEmail = Optional.ofNullable(authServicePort.getAuthenticatedEmail())
                .filter(s -> !s.isBlank())
                        .orElseThrow(() -> new DomainException(EMPLOYEE_EMAIL_NOT_FOUND));

        Order orderResponse = new Order();
        orderResponse.setClientId(clientId);
        orderResponse.setRestaurantId(restaurant.getId());
        orderResponse.setChefId(null);
        orderResponse.setStatus(OrderStatus.PENDIENTE);
        orderResponse.setCreatedAt(LocalDateTime.now());
        orderResponse.setPickupPin(generatePin());
        orderResponse.setItems(order.getItems());

        Order orderSaved = orderPersistencePort.save(orderResponse);

        try {
            Tracelog trace = new Tracelog(
                    orderSaved.getId(),
                    orderSaved.getClientId(),
                    clientEmail,
                    null,
                    null,
                    null,
                    orderSaved.getStatus().name(),
                    Instant.now()
            );

            traceLogOutPort.recordTrace(trace);
        }catch (DomainException e){
            log.warning(e.getMessage());
        }

        return orderSaved;
    }

    @Override
    public Order cancelOrder(Long orderId) {
        Long clientId = authServicePort.getAuthenticatedUserId();

        Order order = orderPersistencePort.findById(orderId)
                .orElseThrow(() -> new DomainException(ORDER_NOT_FOUND));

        if (order.getClientId() == null || !order.getClientId().equals(clientId)) {
            throw new DomainException(ORDER_NOT_OWNED_BY_USER);
        }

        if (order.getStatus() != OrderStatus.PENDIENTE) {
            throw new DomainException(ORDER_ALREADY_IN_PREPARATION);
        }

        String clientEmail = Optional.ofNullable(authServicePort.getAuthenticatedEmail())
                .filter(s -> !s.isBlank())
                .orElseThrow(() -> new DomainException(EMPLOYEE_EMAIL_NOT_FOUND));

        String oldOrderState = order.getStatus().name();
        order.setStatus(OrderStatus.CANCELADO);
        Order orderSaved = orderPersistencePort.save(order);

        try {
            Tracelog trace = new Tracelog(
                    orderSaved.getId(),
                    orderSaved.getClientId(),
                    clientEmail,
                    null,
                    null,
                    oldOrderState,
                    OrderStatus.ENTREGADO.name(),
                    Instant.now());

            traceLogOutPort.recordTrace(trace);
        } catch (Exception ex) {
            log.warning(ex.getMessage());
        }
        return orderSaved;
    }

    @Override
    public List<Tracelog> getMyOrderTrace(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new DomainException("El id de pedido es obligatorio");
        }
        Long clientId = authServicePort.getAuthenticatedUserId();

        Order order = orderPersistencePort.findById(orderId)
                .orElseThrow(() -> new DomainException("Pedido no encontrado"));

        if (!clientId.equals(order.getClientId())) {
            throw new DomainException("No puedes consultar pedidos de otro cliente");
        }
        return traceLogOutPort.findClientTrace(orderId, clientId);
    }

    private String generatePin() {
        var r = Math.random();
        int n = (int)(r * 900000) + 100000;
        return String.valueOf(n);
    }
}
