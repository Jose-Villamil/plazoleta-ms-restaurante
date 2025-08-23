package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.api.IAuthServicePort;
import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.model.Order;
import com.plazoleta.microservicio_plazoleta.domain.model.OrderStatus;
import com.plazoleta.microservicio_plazoleta.domain.spi.IOrderPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IRestaurantEmployeePersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class EmployeeOrderDeliveryUseCaseTest {

    @Mock IAuthServicePort authServicePort;
    @Mock IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort;
    @Mock IOrderPersistencePort orderPersistencePort;

    @InjectMocks
    EmployeeOrderUseCase useCase;

    private static Order order(Long id, Long restaurantId, Long clientId, Long chefId,
                               OrderStatus status, String pin) {
        Order o = new Order();
        o.setId(id);
        o.setRestaurantId(restaurantId);
        o.setClientId(clientId);
        o.setChefId(chefId);
        o.setStatus(status);
        o.setPickupPin(pin);
        return o;
    }

    @Test
    void deliver_happyPath_setsEntregado_andPersists() {
        Long employeeId = 13L;
        Long restaurantId = 5L;
        Long orderId = 77L;
        String pin = "123456";

        when(authServicePort.getAuthenticatedUserId()).thenReturn(employeeId);
        when(restaurantEmployeePersistencePort.findRestaurantIdByEmployeeId(employeeId))
                .thenReturn(Optional.of(restaurantId));
        when(orderPersistencePort.findById(orderId))
                .thenReturn(Optional.of(order(orderId, restaurantId, 40L, employeeId, OrderStatus.LISTO, pin)));

        when(orderPersistencePort.save(any(Order.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Order out = useCase.deliverOrder(orderId, pin);

        assertNotNull(out);
        assertEquals(OrderStatus.ENTREGADO, out.getStatus());
        verify(orderPersistencePort).save(argThat(o ->
                o.getId().equals(orderId) &&
                        o.getStatus() == OrderStatus.ENTREGADO));
    }

    @Test
    void deliver_whenOrderNotFound_throws() {
        when(orderPersistencePort.findById(77L)).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class,
                () -> useCase.deliverOrder(77L, "123456"));

        assertTrue(ex.getMessage().toLowerCase().contains("pedido no encontrado"));
        verify(orderPersistencePort, never()).save(any());
        verifyNoInteractions(authServicePort, restaurantEmployeePersistencePort);
    }


    @Test
    void deliver_whenEmployeeHasNoRestaurant_throws() {
        Long employeeId = 13L;
        Long restaurantId = 5L;
        Long orderId = 77L;
        String pin = "123456";

        when(authServicePort.getAuthenticatedUserId()).thenReturn(13L);
        when(orderPersistencePort.findById(77L))
                .thenReturn(Optional.of(order(orderId, restaurantId, 40L, employeeId, OrderStatus.LISTO, pin)));
        when(restaurantEmployeePersistencePort.findRestaurantIdByEmployeeId(13L))
                .thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class,
                () -> useCase.deliverOrder(77L, "123456"));

        assertTrue(ex.getMessage().contains("empleado no tiene restaurante"));
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    void deliver_whenEmployeeRestaurantDiffers_throws() {
        Long employeeId = 13L;
        when(authServicePort.getAuthenticatedUserId()).thenReturn(employeeId);
        when(restaurantEmployeePersistencePort.findRestaurantIdByEmployeeId(employeeId))
                .thenReturn(Optional.of(5L));

        when(orderPersistencePort.findById(77L))
                .thenReturn(Optional.of(order(77L, 9L, 40L, employeeId, OrderStatus.LISTO, "123456")));

        DomainException ex = assertThrows(DomainException.class,
                () -> useCase.deliverOrder(77L, "123456"));

        assertTrue(ex.getMessage().toLowerCase().contains("otro restaurante"));
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    void deliver_whenStatusNotListo_throws() {
        Long employeeId = 13L, restaurantId = 5L, orderId = 77L;
        when(authServicePort.getAuthenticatedUserId()).thenReturn(employeeId);
        when(restaurantEmployeePersistencePort.findRestaurantIdByEmployeeId(employeeId))
                .thenReturn(Optional.of(restaurantId));
        when(orderPersistencePort.findById(orderId))
                .thenReturn(Optional.of(order(orderId, restaurantId, 40L, employeeId, OrderStatus.PENDIENTE, "123456")));

        DomainException ex = assertThrows(DomainException.class,
                () -> useCase.deliverOrder(orderId, "123456"));

        assertTrue(ex.getMessage().contains("LISTO"));
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    void deliver_whenAlreadyDelivered_throws() {
        Long employeeId = 13L, restaurantId = 5L, orderId = 77L;
        when(authServicePort.getAuthenticatedUserId()).thenReturn(employeeId);
        when(restaurantEmployeePersistencePort.findRestaurantIdByEmployeeId(employeeId))
                .thenReturn(Optional.of(restaurantId));
        when(orderPersistencePort.findById(orderId))
                .thenReturn(Optional.of(order(orderId, restaurantId, 40L, employeeId, OrderStatus.ENTREGADO, "123456")));

        DomainException ex = assertThrows(DomainException.class,
                () -> useCase.deliverOrder(orderId, "123456"));

        assertTrue(ex.getMessage().toLowerCase().contains("entregado"));
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    void deliver_whenPinMismatch_throws() {
        Long employeeId = 13L, restaurantId = 5L, orderId = 77L;
        when(authServicePort.getAuthenticatedUserId()).thenReturn(employeeId);
        when(restaurantEmployeePersistencePort.findRestaurantIdByEmployeeId(employeeId))
                .thenReturn(Optional.of(restaurantId));
        when(orderPersistencePort.findById(orderId))
                .thenReturn(Optional.of(order(orderId, restaurantId, 40L, employeeId, OrderStatus.LISTO, "999999")));

        DomainException ex = assertThrows(DomainException.class,
                () -> useCase.deliverOrder(orderId, "123456"));

        assertTrue(ex.getMessage().toLowerCase().contains("pin"));
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    void deliver_whenPinBlank_throws() {
        DomainException ex = assertThrows(DomainException.class,
                () -> useCase.deliverOrder(77L, " "));

        assertTrue(ex.getMessage().toLowerCase().contains("pin"));
        verify(orderPersistencePort, never()).save(any());
        verifyNoInteractions(authServicePort, restaurantEmployeePersistencePort, orderPersistencePort);

    }
}

