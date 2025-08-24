package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.api.IAuthServicePort;
import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.model.Order;
import com.plazoleta.microservicio_plazoleta.domain.model.OrderStatus;
import com.plazoleta.microservicio_plazoleta.domain.spi.IOrderPersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderUseCaseCancelOrderTest {

    @Mock
    IOrderPersistencePort orderPort;
    @Mock
    IAuthServicePort authPort;

    @InjectMocks
    OrderUseCase useCase;

    private static Order order(Long id, Long clientId, Long restaurantId, OrderStatus status) {
        Order o = new Order();
        o.setId(id);
        o.setClientId(clientId);
        o.setRestaurantId(restaurantId);
        o.setStatus(status);
        o.setItems(List.of());
        return o;
    }

    @Test
    void cancel_whenOrderNotFound_throws() {
        when(orderPort.findById(99L)).thenReturn(Optional.empty());
        assertThrows(DomainException.class, () -> useCase.cancelOrder(99L));
        verify(orderPort, never()).save(any());
    }

    @Test
    void cancel_whenNotOwner_throws() {
        when(authPort.getAuthenticatedUserId()).thenReturn(10L);
        when(orderPort.findById(7L)).thenReturn(Optional.of(order(7L, 20L, 1L, OrderStatus.PENDIENTE)));

        DomainException ex = assertThrows(DomainException.class, () -> useCase.cancelOrder(7L));
        assertTrue(ex.getMessage().toLowerCase().contains("no te pertenece"));

        verify(orderPort, never()).save(any());
    }

    @Test
    void cancel_whenStatusNotPending_throws() {
        when(authPort.getAuthenticatedUserId()).thenReturn(10L);
        when(orderPort.findById(7L)).thenReturn(Optional.of(order(7L, 10L, 1L, OrderStatus.EN_PREPARACION)));

        DomainException ex = assertThrows(DomainException.class, () -> useCase.cancelOrder(7L));
        assertTrue(ex.getMessage().toLowerCase().contains("no puede cancelarse"));

        verify(orderPort, never()).save(any());
    }

    @Test
    void cancel_happyPath_setsCanceled_andPersists() {
        when(authPort.getAuthenticatedUserId()).thenReturn(10L);
        Order db = order(7L, 10L, 1L, OrderStatus.PENDIENTE);
        when(orderPort.findById(7L)).thenReturn(Optional.of(db));

        when(orderPort.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        when(authPort.getAuthenticatedEmail()).thenReturn("cliente@test.com");
        Order out = useCase.cancelOrder(7L);

        assertEquals(OrderStatus.CANCELADO, out.getStatus());
        verify(orderPort).save(argThat(o -> o.getStatus() == OrderStatus.CANCELADO));
    }
}
