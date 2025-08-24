package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.api.IAuthServicePort;
import com.plazoleta.microservicio_plazoleta.domain.api.IEmployeeOrderServicePort;
import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.model.Order;
import com.plazoleta.microservicio_plazoleta.domain.model.OrderStatus;
import com.plazoleta.microservicio_plazoleta.domain.model.Tracelog;
import com.plazoleta.microservicio_plazoleta.domain.model.User;
import com.plazoleta.microservicio_plazoleta.domain.spi.*;
import com.plazoleta.microservicio_plazoleta.domain.util.PageResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.logging.Logger;

import static com.plazoleta.microservicio_plazoleta.domain.util.DomainMessages.*;

public class EmployeeOrderUseCase implements IEmployeeOrderServicePort {
    private final Logger  log = Logger.getLogger(EmployeeOrderUseCase.class.getName());
    private final IAuthServicePort authServicePort;
    private final IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort;
    private final IOrderPersistencePort orderPersistencePort;
    private final IUserPersistencePort  userPersistencePort;
    private final IRestaurantPersistencePort  restaurantPersistencePort;
    private final INotificationOutPort notificationOutPort;
    private final ITraceLogOutPort traceLogOutPort;

    public EmployeeOrderUseCase(IAuthServicePort authServicePort,
                                     IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort,
                                     IOrderPersistencePort orderPersistencePort,
                                IUserPersistencePort userPersistencePort,
                                IRestaurantPersistencePort  restaurantPersistencePort,
                                INotificationOutPort  notificationOutPort, ITraceLogOutPort  traceLogOutPort) {
        this.authServicePort = authServicePort;
        this.restaurantEmployeePersistencePort = restaurantEmployeePersistencePort;
        this.orderPersistencePort = orderPersistencePort;
        this.userPersistencePort = userPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.notificationOutPort = notificationOutPort;
        this.traceLogOutPort = traceLogOutPort;
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
    @Transactional
    public Order assignSelfToOrder(Long orderId) {

        if (orderId == null || orderId <= 0) {
            throw new DomainException(String.format(FIELD_REQUIRED, "Id de pedido"));
        }

        Long employeeId = authServicePort.getAuthenticatedUserId();
        Long restaurantId = restaurantEmployeePersistencePort.findRestaurantIdByEmployeeId(employeeId)
                .orElseThrow(() -> new DomainException(EMPLOYEE_WITHOUT_RESTAURANT));
        Order order = orderPersistencePort.findById(orderId)
                .orElseThrow(() -> new DomainException(ORDER_NOT_FOUND));

        if (!restaurantId.equals(order.getRestaurantId())) {
            throw new DomainException("No puedes gestionar pedidos de otro restaurante");
        }
        if (order.getChefId() != null) {
            throw new DomainException("El pedido ya tiene un empleado asignado");
        }
        if (order.getStatus() != OrderStatus.PENDIENTE) {
            throw new DomainException("Solo se pueden asignar pedidos en estado PENDIENTE");
        }

        String employeeEmail = Optional.ofNullable(authServicePort.getAuthenticatedEmail())
                .filter(s -> !s.isBlank())
                .orElseGet(() -> userPersistencePort.findById(employeeId)
                        .map(User::getEmail)
                        .orElseThrow(() -> new DomainException("No se pudo obtener el email del empleado")));

        String clientEmail = userPersistencePort.findById(order.getClientId())
                .map(User::getEmail)
                .orElseThrow(() -> new DomainException("No se pudo obtener el email del cliente"));

        String oldOrderState = order.getStatus().name();
        order.setChefId(employeeId);
        order.setStatus(OrderStatus.EN_PREPARACION);

        Order orderSaved = orderPersistencePort.save(order);

        try {
            Tracelog trace = new Tracelog(
                    orderSaved.getId(),
                    orderSaved.getClientId(),
                    clientEmail,
                    employeeId,
                    employeeEmail,
                    oldOrderState,
                    orderSaved.getStatus().name(),
                    Instant.now());

            traceLogOutPort.recordTrace(trace);
        }catch (Exception ex){
            log.warning(ex.getMessage());
        }

        return orderSaved;
    }

    @Override
    @Transactional
    public Order markOrderAsReady(Long orderId) {
        Long employeeId = authServicePort.getAuthenticatedUserId();

        Order order = orderPersistencePort.findById(orderId)
                .orElseThrow(() -> new DomainException("Pedido no encontrado"));

        if (order.getStatus() != OrderStatus.EN_PREPARACION) {
            throw new DomainException("Solo pedidos EN_PREPARACION pueden pasar a LISTO");
        }
        if (order.getChefId() == null || !order.getChefId().equals(employeeId)) {
            throw new DomainException("Solo el empleado asignado puede marcarlo como LISTO");
        }

        Long restaurantId = restaurantEmployeePersistencePort.findRestaurantIdByEmployeeId(employeeId)
                .orElseThrow(() -> new DomainException(EMPLOYEE_WITHOUT_RESTAURANT));
        if (!restaurantId.equals(order.getRestaurantId())) {
            throw new DomainException("No puedes operar pedidos de otro restaurante");
        }

        String oldOrderState = order.getStatus().name();
        order.setStatus(OrderStatus.LISTO);
        Order orderSaved = orderPersistencePort.save(order);

        var restaurant = restaurantPersistencePort.findRestaurantById(order.getRestaurantId())
                .orElseThrow(() -> new DomainException(RESTAURANT_NOT_FOUND));

        var client = userPersistencePort.findById(order.getClientId())
                .orElseThrow(() -> new DomainException(CLIENT_NOT_FOUND));

        String employeeEmail = Optional.ofNullable(authServicePort.getAuthenticatedEmail())
                .filter(s -> !s.isBlank())
                .orElseGet(() -> userPersistencePort.findById(employeeId)
                        .map(User::getEmail)
                        .orElseThrow(() -> new DomainException(EMPLOYEE_EMAIL_NOT_FOUND)));

        try {
            notificationOutPort.sendOrderReady(
                    orderSaved.getId(), client.getPhoneNumber(), orderSaved.getPickupPin(),
                    restaurant.getId(), restaurant.getName()
            );
            Tracelog trace = new Tracelog(
                    orderSaved.getId(),
                    orderSaved.getClientId(),
                    client.getEmail(),
                    employeeId,
                    employeeEmail,
                    oldOrderState,
                    OrderStatus.LISTO.name(),
                    Instant.now());

            traceLogOutPort.recordTrace(trace);
        } catch (Exception ex) {
            //reintento asíncrono (outbox/evento) sin romper el flujo del pedido
            log.warning(ex.getMessage());
        }

        return orderSaved;
    }

    @Override
    public Order deliverOrder(Long orderId, String pin) {

        if (orderId == null || orderId <= 0) {
            throw new DomainException(String.format(FIELD_REQUIRED,"orderId"));
        }
        if (pin == null || pin.isBlank()) {
            throw new DomainException("El PIN es obligatorio");
        }

        Order order = orderPersistencePort.findById(orderId)
                .orElseThrow(() -> new DomainException(ORDER_NOT_FOUND));

        Long employeeId = authServicePort.getAuthenticatedUserId();

        Long restaurantId = restaurantEmployeePersistencePort.findRestaurantIdByEmployeeId(employeeId)
                .orElseThrow(() -> new DomainException(EMPLOYEE_WITHOUT_RESTAURANT));
        if (!restaurantId.equals(order.getRestaurantId())) {
            throw new DomainException("No puedes operar pedidos de otro restaurante");
        }

        if (order.getStatus() != OrderStatus.LISTO) {
            throw new DomainException("Solo pedidos en estado LISTO pueden ser entregados");
        }

        if (order.getChefId() == null || !order.getChefId().equals(employeeId)) {
            throw new DomainException("Solo el empleado asignado puede entregar este pedido");
        }

        if (!pin.equals(order.getPickupPin())) {
            throw new DomainException(String.format(FIELD_INVALID, "PIN"));
        }
        var client = userPersistencePort.findById(order.getClientId())
                .orElseThrow(() -> new DomainException(CLIENT_NOT_FOUND));

        String employeeEmail = Optional.ofNullable(authServicePort.getAuthenticatedEmail())
                .filter(s -> !s.isBlank())
                .orElseGet(() -> userPersistencePort.findById(employeeId)
                        .map(User::getEmail)
                        .orElseThrow(() -> new DomainException(EMPLOYEE_EMAIL_NOT_FOUND)));

        String oldOrderState = order.getStatus().name();
        order.setStatus(OrderStatus.ENTREGADO);

        Order orderSaved = orderPersistencePort.save(order);

        try {
            Tracelog trace = new Tracelog(
                    orderSaved.getId(),
                    orderSaved.getClientId(),
                    client.getEmail(),
                    employeeId,
                    employeeEmail,
                    oldOrderState,
                    OrderStatus.ENTREGADO.name(),
                    Instant.now());

            traceLogOutPort.recordTrace(trace);
        } catch (Exception ex) {
            //reintento asíncrono (outbox/evento) sin romper el flujo del pedido
            log.warning(ex.getMessage());
        }
        return orderSaved;
    }
}
