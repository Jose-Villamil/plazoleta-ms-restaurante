package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.model.Order;
import com.plazoleta.microservicio_plazoleta.domain.model.OrderStatus;
import com.plazoleta.microservicio_plazoleta.domain.model.Tracelog;
import com.plazoleta.microservicio_plazoleta.domain.spi.IOrderPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.ITraceLogOutPort;
import com.plazoleta.microservicio_plazoleta.domain.api.IAuthServicePort;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientTraceLogUseCaseTest {

    @Mock private IAuthServicePort authServicePort;
    @Mock private IOrderPersistencePort orderPersistencePort;
    @Mock private ITraceLogOutPort traceLogOutPort;

    @InjectMocks
    private OrderUseCase useCase;

    private static Order order(Long id, Long clientId, Long restaurantId) {
        Order o = new Order();
        o.setId(id);
        o.setClientId(clientId);
        o.setRestaurantId(restaurantId);
        o.setStatus(OrderStatus.PENDIENTE);
        return o;
    }

    private static Tracelog log(String id, Long orderId, Long clientId, String oldSt, String newSt) {
        Tracelog t = new Tracelog();
        t.setOrderId(orderId);
        t.setClientId(clientId);
        t.setEmployeeId(null);
        t.setOldStatus(oldSt);
        t.setNewStatus(newSt);
        t.setAt(Instant.now());
        return t;
    }

    @Test
    void getMyOrderTrace_whenOrderIdInvalid_throws() {
        DomainException ex1 = assertThrows(DomainException.class, () -> useCase.getMyOrderTrace(null));
        DomainException ex2 = assertThrows(DomainException.class, () -> useCase.getMyOrderTrace(0L));

        assertTrue(ex1.getMessage().toLowerCase().contains("obligatorio"));
        assertTrue(ex2.getMessage().toLowerCase().contains("obligatorio"));

        verifyNoInteractions(authServicePort, orderPersistencePort, traceLogOutPort);
    }

    @Test
    void getMyOrderTrace_whenOrderNotFound_throws() {
        Long clientId = 42L;
        when(authServicePort.getAuthenticatedUserId()).thenReturn(clientId);
        when(orderPersistencePort.findById(99L)).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class, () -> useCase.getMyOrderTrace(99L));
        assertTrue(ex.getMessage().toLowerCase().contains("pedido no encontrado"));

        verify(orderPersistencePort).findById(99L);
        verifyNoInteractions(traceLogOutPort);
    }

    @Test
    void getMyOrderTrace_whenOrderNotOwned_throws() {
        Long callerId = 42L;
        when(authServicePort.getAuthenticatedUserId()).thenReturn(callerId);
        when(orderPersistencePort.findById(10L)).thenReturn(Optional.of(order(10L, 77L, 1L)));

        DomainException ex = assertThrows(DomainException.class, () -> useCase.getMyOrderTrace(10L));
        assertTrue(ex.getMessage().toLowerCase().contains("otro cliente"));

        verify(orderPersistencePort).findById(10L);
        verifyNoInteractions(traceLogOutPort);
    }

    @Test
    void getMyOrderTrace_happyPath_returnsLogs() {
        Long callerId = 42L;
        Long orderId = 10L;

        when(authServicePort.getAuthenticatedUserId()).thenReturn(callerId);
        when(orderPersistencePort.findById(orderId))
                .thenReturn(Optional.of(order(orderId, callerId, 1L)));

        List<Tracelog> logs = List.of(
                log("a", orderId, callerId, null, "PENDIENTE"),
                log("b", orderId, callerId, "PENDIENTE", "EN_PREPARACION")
        );
        when(traceLogOutPort.findClientTrace(orderId, callerId)).thenReturn(logs);

        var out = useCase.getMyOrderTrace(orderId);

        assertNotNull(out);
        assertEquals(2, out.size());
        verify(traceLogOutPort).findClientTrace(orderId, callerId);
    }
}

