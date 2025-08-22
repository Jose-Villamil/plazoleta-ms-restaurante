package com.plazoleta.microservicio_plazoleta.domain.usecase;


import com.plazoleta.microservicio_plazoleta.domain.api.IAuthServicePort;
import com.plazoleta.microservicio_plazoleta.domain.api.IOrderServicePort;
import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.model.*;
import com.plazoleta.microservicio_plazoleta.domain.spi.IDishPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IOrderPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IRestaurantPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.util.Constantes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;

public class OrderUseCase implements IOrderServicePort {

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
    public Order saveOrder(Order draft) {
        // 1) Cliente autenticado
        Long clientId = authServicePort.getAuthenticatedUserId();

        // 2) Validaciones básicas
        if (draft == null || draft.getRestaurantId() == null) {
            throw new DomainException(String.format("El campo %s no puede ser nulo o vacío", "Restaurante"));
        }
        if (draft.getItems() == null || draft.getItems().isEmpty()) {
            throw new DomainException("El pedido debe contener al menos un plato");
        }

        // 3) Validar no tiene pedido en proceso
        if (orderPersistencePort.clientHasOpenOrder(clientId)) {
            throw new DomainException("Ya tienes un pedido en proceso");
        }

        // 4) Validar restaurante existente
        Restaurant r = restaurantPersistencePort.findRestaurantById(draft.getRestaurantId())
                .orElseThrow(() -> new DomainException("No se encontró el restaurante"));

        // 5) Validar platos: pertenecen al mismo restaurante y están activos
        var seenDishes = new HashSet<Long>();
        for (OrderItem it : draft.getItems()) {
            if (it.getDishId() == null || it.getQuantity() <= 0) {
                throw new DomainException("Cada ítem debe tener plato y cantidad > 0");
            }
            if (!seenDishes.add(it.getDishId())) {
                throw new DomainException("No se permiten platos repetidos en el pedido");
            }

            Dish dish = dishPersistencePort.findDishById(it.getDishId())
                    .orElseThrow(() -> new DomainException("No se encontró el plato"));

            if (!Boolean.TRUE.equals(dish.isActive())) {
                throw new DomainException("Uno de los platos no está activo");
            }
            if (!draft.getRestaurantId().equals(dish.getRestaurantId())) {
                throw new DomainException("Todos los platos deben ser del mismo restaurante");
            }
        }

        // 6) Completar datos del pedido
        Order order = new Order();
        order.setClientId(clientId);
        order.setRestaurantId(r.getId());
        order.setChefId(null); // se asignará en HU13
        order.setStatus(OrderStatus.PENDIENTE);
        order.setCreatedAt(LocalDateTime.now());
        order.setPickupPin(generatePin()); // para HU15
        order.setItems(draft.getItems());

        // 7) Guardar
        return orderPersistencePort.save(order);
    }

    private String generatePin() {
        // 6 dígitos
        var r = Math.random();
        int n = (int)(r * 900000) + 100000;
        return String.valueOf(n);
    }
}
