package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.api.IAuthServicePort;
import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.model.Order;
import com.plazoleta.microservicio_plazoleta.domain.model.OrderStatus;
import com.plazoleta.microservicio_plazoleta.domain.model.User;
import com.plazoleta.microservicio_plazoleta.domain.spi.IRestaurantEmployeePersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IOrderPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.ITraceLogOutPort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IUserPersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeOrderAssignTest {

    @Mock private IAuthServicePort authServicePort;
    @Mock private IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort;
    @Mock private IOrderPersistencePort orderPersistencePort;
    @Mock private IUserPersistencePort  userPersistencePort;
    @Mock
    ITraceLogOutPort traceLogOutPort;

    @InjectMocks
    private EmployeeOrderUseCase employeeOrderUseCase;

    private static Order pendingOrder(Long id, Long restaurantId) {
        Order o = new Order();
        o.setId(id);
        o.setRestaurantId(restaurantId);
        o.setStatus(OrderStatus.PENDIENTE);
        o.setChefId(null);
        return o;
    }

    @Test
    void whenOrderIdInvalid_throws() {
        DomainException ex1 = assertThrows(DomainException.class, () -> employeeOrderUseCase.assignSelfToOrder(null));
        assertTrue(ex1.getMessage().toLowerCase().contains("id de pedido"));

        DomainException ex2 = assertThrows(DomainException.class, () -> employeeOrderUseCase.assignSelfToOrder(0L));
        assertTrue(ex2.getMessage().toLowerCase().contains("id de pedido"));

        verifyNoInteractions(authServicePort, restaurantEmployeePersistencePort, orderPersistencePort);
    }

    @Test
    void whenEmployeeHasNoRestaurant_throws() {
        when(authServicePort.getAuthenticatedUserId()).thenReturn(30L);
        when(restaurantEmployeePersistencePort.findRestaurantIdByEmployeeId(30L)).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class, () -> employeeOrderUseCase.assignSelfToOrder(5L));
        assertTrue(ex.getMessage().toLowerCase().contains("no tiene restaurante"));
        verifyNoInteractions(orderPersistencePort);
    }

    @Test
    void whenOrderNotFound_throws() {
        when(authServicePort.getAuthenticatedUserId()).thenReturn(30L);
        when(restaurantEmployeePersistencePort.findRestaurantIdByEmployeeId(30L)).thenReturn(Optional.of(9L));
        when(orderPersistencePort.findById(5L)).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class, () -> employeeOrderUseCase.assignSelfToOrder(5L));
        assertTrue(ex.getMessage().toLowerCase().contains("pedido"));
        verify(orderPersistencePort).findById(5L);
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    void whenOrderBelongsToAnotherRestaurant_throws() {
        when(authServicePort.getAuthenticatedUserId()).thenReturn(30L);
        when(restaurantEmployeePersistencePort.findRestaurantIdByEmployeeId(30L)).thenReturn(Optional.of(9L));
        Order order = pendingOrder(5L, 99L);
        when(orderPersistencePort.findById(5L)).thenReturn(Optional.of(order));

        DomainException ex = assertThrows(DomainException.class, () -> employeeOrderUseCase.assignSelfToOrder(5L));
        assertTrue(ex.getMessage().toLowerCase().contains("otro restaurante"));
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    void whenOrderAlreadyAssigned_throws() {
        when(authServicePort.getAuthenticatedUserId()).thenReturn(30L);
        when(restaurantEmployeePersistencePort.findRestaurantIdByEmployeeId(30L)).thenReturn(Optional.of(9L));
        Order order = pendingOrder(5L, 9L);
        order.setChefId(77L);
        when(orderPersistencePort.findById(5L)).thenReturn(Optional.of(order));

        DomainException ex = assertThrows(DomainException.class, () -> employeeOrderUseCase.assignSelfToOrder(5L));
        assertTrue(ex.getMessage().toLowerCase().contains("ya tiene un empleado"));
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    void whenOrderNotPending_throws() {
        when(authServicePort.getAuthenticatedUserId()).thenReturn(30L);
        when(restaurantEmployeePersistencePort.findRestaurantIdByEmployeeId(30L)).thenReturn(Optional.of(9L));
        Order order = pendingOrder(5L, 9L);
        order.setStatus(OrderStatus.EN_PREPARACION);
        when(orderPersistencePort.findById(5L)).thenReturn(Optional.of(order));

        DomainException ex = assertThrows(DomainException.class, () -> employeeOrderUseCase.assignSelfToOrder(5L));
        assertTrue(ex.getMessage().toLowerCase().contains("solo se pueden asignar"));
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    void happyPath_setsChefAndStatus_andSaves() {
            Long employeeId = 30L;
            Long restaurantId = 9L;
            Long orderId = 5L;
            Long clientId = 101L;

            Order order = pendingOrder(orderId, restaurantId);
            order.setClientId(clientId);

            when(authServicePort.getAuthenticatedUserId()).thenReturn(employeeId);
            when(authServicePort.getAuthenticatedEmail()).thenReturn("empleado@test.com");
            when(restaurantEmployeePersistencePort.findRestaurantIdByEmployeeId(employeeId))
                    .thenReturn(Optional.of(restaurantId));
            when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(order));

            User client = new User();
            client.setId(clientId);
            client.setEmail("cliente@test.com");
            when(userPersistencePort.findById(clientId)).thenReturn(Optional.of(client));

            doNothing().when(traceLogOutPort).recordTrace(any());

            when(orderPersistencePort.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            Order updated = employeeOrderUseCase.assignSelfToOrder(orderId);

            assertEquals(employeeId, updated.getChefId());
            assertEquals(OrderStatus.EN_PREPARACION, updated.getStatus());

            verify(orderPersistencePort).save(argThat(o ->
                    o.getId().equals(orderId)
                            && o.getRestaurantId().equals(restaurantId)
                            && o.getChefId().equals(employeeId)
                            && o.getStatus() == OrderStatus.EN_PREPARACION
            ));

            verifyNoMoreInteractions(orderPersistencePort, restaurantEmployeePersistencePort,
                    authServicePort, userPersistencePort, traceLogOutPort);
    }
}

