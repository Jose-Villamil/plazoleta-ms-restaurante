package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.api.IAuthServicePort;
import com.plazoleta.microservicio_plazoleta.domain.api.IEmployeeOrderServicePort;
import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.model.Order;
import com.plazoleta.microservicio_plazoleta.domain.model.OrderStatus;
import com.plazoleta.microservicio_plazoleta.domain.spi.*;
import com.plazoleta.microservicio_plazoleta.domain.util.PageResult;

import java.time.LocalDateTime;
import java.util.logging.Logger;

import static com.plazoleta.microservicio_plazoleta.domain.util.DomainMessages.EMPLOYEE_WITHOUT_RESTAURANT;
import static com.plazoleta.microservicio_plazoleta.domain.util.DomainMessages.FIELD_REQUIRED;

public class EmployeeOrderUseCase implements IEmployeeOrderServicePort {
    private final Logger  log = Logger.getLogger(EmployeeOrderUseCase.class.getName());
    private final IAuthServicePort authServicePort;
    private final IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort;
    private final IOrderPersistencePort orderPersistencePort;
    private final IUserPersistencePort  userPersistencePort;
    private final IRestaurantPersistencePort  restaurantPersistencePort;
    private final INotificationOutPort notificationOutPort;

    public EmployeeOrderUseCase(IAuthServicePort authServicePort,
                                     IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort,
                                     IOrderPersistencePort orderPersistencePort,
                                IUserPersistencePort userPersistencePort,
                                IRestaurantPersistencePort  restaurantPersistencePort,
                                INotificationOutPort  notificationOutPort) {
        this.authServicePort = authServicePort;
        this.restaurantEmployeePersistencePort = restaurantEmployeePersistencePort;
        this.orderPersistencePort = orderPersistencePort;
        this.userPersistencePort = userPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.notificationOutPort = notificationOutPort;
    }

    @Override
    public PageResult<Order> listOrdersByStatus(OrderStatus status, int page, int size) {
        if (status == null) throw new DomainException(String.format(FIELD_REQUIRED, "Estado"));
        if (page < 0) page = 0;
        if (size <= 0) size = 10;

        Long employeeId = authServicePort.getAuthenticatedUserId();

        Long restaurantId = restaurantEmployeePersistencePort.findRestaurantIdByEmployeeId(employeeId)
                .orElseThrow(() -> new DomainException(EMPLOYEE_WITHOUT_RESTAURANT));

        return orderPersistencePort.findByRestaurantAndStatus(restaurantId, status, page, size);
    }

    @Override
    public Order assignSelfToOrder(Long orderId) {

        if (orderId == null || orderId <= 0) {
            throw new DomainException(String.format(FIELD_REQUIRED, "Id de pedido"));
        }
        Long employeeId = authServicePort.getAuthenticatedUserId();
        Long restaurantId = restaurantEmployeePersistencePort.findRestaurantIdByEmployeeId(employeeId)
                .orElseThrow(() -> new DomainException(EMPLOYEE_WITHOUT_RESTAURANT));
        Order order = orderPersistencePort.findById(orderId)
                .orElseThrow(() -> new DomainException("No se encontró el pedido"));


        if (!restaurantId.equals(order.getRestaurantId())) {
            throw new DomainException("No puedes gestionar pedidos de otro restaurante");
        }

        if (order.getChefId() != null) {
            throw new DomainException("El pedido ya tiene un empleado asignado");
        }
        if (order.getStatus() != OrderStatus.PENDIENTE) {
            throw new DomainException("Solo se pueden asignar pedidos en estado PENDIENTE");
        }

        order.setChefId(employeeId);
        order.setStatus(OrderStatus.EN_PREPARACION);

        return orderPersistencePort.save(order);
    }

    public Order markOrderAsReady(Long orderId) {
        Long employeeId = authServicePort.getAuthenticatedUserId();

        Order o = orderPersistencePort.findById(orderId)
                .orElseThrow(() -> new DomainException("Pedido no encontrado"));

        Long restaurantId = restaurantEmployeePersistencePort.findRestaurantIdByEmployeeId(employeeId)
                .orElseThrow(() -> new DomainException(EMPLOYEE_WITHOUT_RESTAURANT));
        if (!restaurantId.equals(o.getRestaurantId())) {
            throw new DomainException("No puedes operar pedidos de otro restaurante");
        }

        if (o.getStatus() != OrderStatus.EN_PREPARACION) {
            throw new DomainException("Solo pedidos EN_PREPARACION pueden pasar a LISTO");
        }
        if (o.getChefId() == null || !o.getChefId().equals(employeeId)) {
            throw new DomainException("Solo el empleado asignado puede marcarlo como LISTO");
        }

        o.setStatus(OrderStatus.LISTO);
        o.setCreatedAt(LocalDateTime.now());
        orderPersistencePort.save(o);

        var client = userPersistencePort.findById(o.getClientId())
                .orElseThrow(() -> new DomainException("Cliente no encontrado"));
        String clientPhone = client.getPhoneNumber();

        var restaurant = restaurantPersistencePort.findRestaurantById(o.getRestaurantId())
                .orElseThrow(() -> new DomainException("Restaurante no encontrado"));

        try {
            notificationOutPort.sendOrderReady(
                    o.getId(), clientPhone, o.getPickupPin(),
                    restaurant.getId(), restaurant.getName()
            );
        } catch (Exception ex) {
            //reintento asíncrono (outbox/evento) sin romper el flujo del pedido
            log.warning("Fallo enviando SMS de pedido listo" + ex.getMessage());
        }

        return o;
    }

    @Override
    public Order deliverOrder(Long orderId, String pin) {

        if (orderId == null || orderId <= 0) {
            throw new DomainException("El id del pedido es obligatorio");
        }
        if (pin == null || pin.isBlank()) {
            throw new DomainException("El PIN es obligatorio");
        }

        Order o = orderPersistencePort.findById(orderId)
                .orElseThrow(() -> new DomainException("Pedido no encontrado"));

        Long employeeId = authServicePort.getAuthenticatedUserId();

        Long restaurantId = restaurantEmployeePersistencePort.findRestaurantIdByEmployeeId(employeeId)
                .orElseThrow(() -> new DomainException("El empleado no tiene restaurante asociado"));
        if (!restaurantId.equals(o.getRestaurantId())) {
            throw new DomainException("No puedes operar pedidos de otro restaurante");
        }

        if (o.getStatus() != OrderStatus.LISTO) {
            throw new DomainException("Solo pedidos en estado LISTO pueden ser entregados");
        }

        if (o.getChefId() == null || !o.getChefId().equals(employeeId)) {
            throw new DomainException("Solo el empleado asignado puede entregar este pedido");
        }

        if (!pin.equals(o.getPickupPin())) {
            throw new DomainException("PIN inválido");
        }

        o.setStatus(OrderStatus.ENTREGADO);
        // o.setDeliveredAt(LocalDateTime.now());
        // (opcional) invalidar PIN
        // o.setPickupPin(null);

        orderPersistencePort.save(o);
        return o;
    }
}
