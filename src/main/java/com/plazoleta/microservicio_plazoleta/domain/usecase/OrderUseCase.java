package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.api.IAuthServicePort;
import com.plazoleta.microservicio_plazoleta.domain.api.IOrderServicePort;
import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.model.*;
import com.plazoleta.microservicio_plazoleta.domain.spi.IDishPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IOrderPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IRestaurantPersistencePort;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.plazoleta.microservicio_plazoleta.domain.util.DomainMessages.*;

public class OrderUseCase implements IOrderServicePort {

    private static final Set<OrderStatus> IN_PROGRESS = Set.of(
            OrderStatus.PENDIENTE, OrderStatus.EN_PREPARACION, OrderStatus.LISTO
    );

    private final IOrderPersistencePort orderPersistencePort;
    private final IAuthServicePort authServicePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IDishPersistencePort dishPersistencePort;

    public OrderUseCase(IOrderPersistencePort orderPort,
                        IAuthServicePort authPort,
                        IRestaurantPersistencePort restaurantPort,
                        IDishPersistencePort dishPort) {
        this.orderPersistencePort = orderPort;
        this.authServicePort = authPort;
        this.restaurantPersistencePort = restaurantPort;
        this.dishPersistencePort = dishPort;
    }

    @Override
    public Order saveOrder(Order order) {
        Long clientId = authServicePort.getAuthenticatedUserId();

        if (order == null || order.getRestaurantId() == null) {
            throw new DomainException(String.format(FIELD_REQUIRED, "Restaurante"));
        }
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new DomainException("El pedido debe contener al menos un plato");
        }

        if (orderPersistencePort.existsByClientAndStatuses(clientId, IN_PROGRESS)) {
            throw new DomainException("Ya tienes un pedido en proceso");
        }
        Restaurant r = restaurantPersistencePort.findRestaurantById(order.getRestaurantId())
                .orElseThrow(() -> new DomainException(RESTAURANT_NOT_FOUND));
        var seenDishes = new HashSet<Long>();
        for (OrderItem it : order.getItems()) {
            if (it.getDishId() == null || it.getQuantity() <= 0) {
                throw new DomainException("Cada ítem debe tener plato y cantidad > 0");
            }
            if (!seenDishes.add(it.getDishId())) {
                throw new DomainException("No se permiten platos repetidos en el pedido");
            }

            Dish dish = dishPersistencePort.findDishById(it.getDishId())
                    .orElseThrow(() -> new DomainException(DISH_NOT_FOUND));

            if (!dish.isActive()) {
                throw new DomainException("Uno de los platos no está activo");
            }
            if (!order.getRestaurantId().equals(dish.getRestaurantId())) {
                throw new DomainException("Todos los platos deben ser del mismo restaurante");
            }
        }

        Order orderResponse = new Order();
        orderResponse.setClientId(clientId);
        orderResponse.setRestaurantId(r.getId());
        orderResponse.setChefId(null);
        orderResponse.setStatus(OrderStatus.PENDIENTE);
        orderResponse.setCreatedAt(LocalDateTime.now());
        orderResponse.setPickupPin(generatePin());
        orderResponse.setItems(order.getItems());
        return orderPersistencePort.save(orderResponse);
    }

    private String generatePin() {
        var r = Math.random();
        int n = (int)(r * 900000) + 100000;
        return String.valueOf(n);
    }
}
