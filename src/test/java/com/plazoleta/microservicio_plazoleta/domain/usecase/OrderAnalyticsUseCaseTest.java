package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.api.IAuthServicePort;
import com.plazoleta.microservicio_plazoleta.domain.api.IOrderAnalyticsServicePort.EmployeeEfficiency;
import com.plazoleta.microservicio_plazoleta.domain.api.IOrderAnalyticsServicePort.OrderDuration;
import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.model.Order;
import com.plazoleta.microservicio_plazoleta.domain.model.OrderStatus;
import com.plazoleta.microservicio_plazoleta.domain.model.Restaurant;
import com.plazoleta.microservicio_plazoleta.domain.model.Tracelog;
import com.plazoleta.microservicio_plazoleta.domain.spi.IOrderPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IRestaurantPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.ITraceLogOutPort;
import com.plazoleta.microservicio_plazoleta.domain.util.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static com.plazoleta.microservicio_plazoleta.domain.util.DomainMessages.NOT_RESTAURANT_OWNER;
import static com.plazoleta.microservicio_plazoleta.domain.util.DomainMessages.RESTAURANT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderAnalyticsUseCaseTest {

    @Mock IAuthServicePort auth;
    @Mock IRestaurantPersistencePort restaurantPort;
    @Mock IOrderPersistencePort orderPort;
    @Mock ITraceLogOutPort tracePort;

    @InjectMocks OrderAnalyticsUseCase useCase;

    private final Long OWNER_ID = 10L;
    private final Long RESTAURANT_ID = 99L;

    @BeforeEach
    void setup() {
        when(auth.getAuthenticatedUserId()).thenReturn(OWNER_ID);

        Restaurant r = new Restaurant();
        r.setId(RESTAURANT_ID);
        r.setIdOwner(OWNER_ID);
        when(restaurantPort.findRestaurantById(RESTAURANT_ID)).thenReturn(Optional.of(r));
    }

    @Test
    void listOrderDurations_happyPath_usesTracesTimes() {

        Order o = order(1L, 100L, RESTAURANT_ID, OrderStatus.ENTREGADO, 20L,
                LocalDateTime.of(2025, 8, 24, 12, 0));

        Instant t1 = Instant.parse("2025-08-24T12:05:00Z");
        Instant t2 = Instant.parse("2025-08-24T12:25:30Z");

        when(orderPort.findByRestaurantAndStatus(RESTAURANT_ID, OrderStatus.ENTREGADO, 0, 100))
                .thenReturn(page(List.of(o)));
        when(tracePort.findClientTrace(1L, 100L))
                .thenReturn(List.of(trace(o, "EN_PREPARACION", t1), trace(o, "ENTREGADO", t2)));

        List<OrderDuration> out = useCase.listOrderDurations(RESTAURANT_ID, 0, 100);

        assertEquals(1, out.size());
        var d = out.get(0);
        assertEquals(1L, d.orderId());
        assertEquals(20L, d.chefId());
        assertEquals(t1, d.startedAt());
        assertEquals(t2, d.finishedAt());
        assertEquals(1230L, d.durationSeconds());
    }

    @Test
    void listOrderDurations_fallbackToCreatedAt_whenNoStartTrace() {
        LocalDateTime createdAt = LocalDateTime.of(2025, 8, 24, 10, 0);
        Order o = order(2L, 200L, RESTAURANT_ID, OrderStatus.ENTREGADO, 30L, createdAt);

        Instant delivered = Instant.parse("2025-08-24T10:20:00Z");
        Instant createdAtUtc = createdAt.toInstant(ZoneOffset.UTC);

        when(orderPort.findByRestaurantAndStatus(RESTAURANT_ID, OrderStatus.ENTREGADO, 0, 50))
                .thenReturn(page(List.of(o)));
        when(tracePort.findClientTrace(2L, 200L))
                .thenReturn(List.of(trace(o, "ENTREGADO", delivered)));

        List<OrderDuration> out = useCase.listOrderDurations(RESTAURANT_ID, 0, 50);

        assertEquals(1, out.size());
        var d = out.get(0);
        assertEquals(createdAtUtc, d.startedAt());
        assertEquals(delivered, d.finishedAt());
        assertEquals(20 * 60L, d.durationSeconds());
    }

    @Test
    void listOrderDurations_skipsOrdersWithoutDeliveredTrace() {
        Order o = order(3L, 300L, RESTAURANT_ID, OrderStatus.ENTREGADO, 40L,
                LocalDateTime.of(2025, 8, 24, 11, 0));

        when(orderPort.findByRestaurantAndStatus(RESTAURANT_ID, OrderStatus.ENTREGADO, 0, 10))
                .thenReturn(page(List.of(o)));

        when(tracePort.findClientTrace(3L, 300L))
                .thenReturn(List.of(trace(o, "EN_PREPARACION", Instant.parse("2025-08-24T11:05:00Z"))));

        List<OrderDuration> out = useCase.listOrderDurations(RESTAURANT_ID, 0, 10);

        assertTrue(out.isEmpty());
    }

    @Test
    void employeeRanking_ignoresNullChef_andAppliesMinOrders() {
        Order oNoChef = order(31L, 3001L, RESTAURANT_ID, OrderStatus.ENTREGADO, null, LocalDateTime.now());
        Order oChef = order(32L, 3002L, RESTAURANT_ID, OrderStatus.ENTREGADO, 555L, LocalDateTime.now());

        when(orderPort.findByRestaurantAndStatus(RESTAURANT_ID, OrderStatus.ENTREGADO, 0, 10))
                .thenReturn(page(List.of(oNoChef, oChef)));

        stubDurations(oNoChef, 10, oNoChef.getClientId());
        stubDurations(oChef, 15, oChef.getClientId());

        List<EmployeeEfficiency> out = useCase.employeeRanking(RESTAURANT_ID, 0, 10, 2);

        assertTrue(out.isEmpty());
    }

    @Test
    void listOrderDurations_throwsWhenNotOwner() {
        Restaurant r = new Restaurant();
        r.setId(RESTAURANT_ID);
        r.setIdOwner(777L);
        when(restaurantPort.findRestaurantById(RESTAURANT_ID)).thenReturn(Optional.of(r));

        DomainException ex = assertThrows(DomainException.class,
                () -> useCase.listOrderDurations(RESTAURANT_ID, 0, 10));
        assertEquals(NOT_RESTAURANT_OWNER, ex.getMessage());
        verifyNoInteractions(orderPort, tracePort);
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void listOrderDurations_throwsWhenRestaurantNotFound() {
        when(restaurantPort.findRestaurantById(RESTAURANT_ID)).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class,
                () -> useCase.listOrderDurations(RESTAURANT_ID, 0, 10));
        assertEquals(RESTAURANT_NOT_FOUND, ex.getMessage());
        verifyNoInteractions(orderPort, tracePort);
    }

    private PageResult<Order> page(List<Order> items) {
        return new PageResult<>(items, 0, items.size(), items.size(), 1);
    }

    private Order order(Long id, Long clientId, Long restaurantId, OrderStatus status, Long chefId, LocalDateTime createdAt) {
        Order o = new Order();
        o.setId(id);
        o.setClientId(clientId);
        o.setRestaurantId(restaurantId);
        o.setStatus(status);
        o.setChefId(chefId);
        o.setCreatedAt(createdAt);
        return o;
    }

    private Tracelog trace(Order o, String newStatus, Instant at) {
        Tracelog t = new Tracelog();
        t.setClientId(o.getClientId());
        t.setEmployeeId(o.getChefId());
        t.setOldStatus(null);
        t.setNewStatus(newStatus);
        t.setAt(at);
        t.setOrderId(o.getId());
        return t;
    }

    private void stubDurations(Order o, int minutesToDeliver, Long clientId) {
        Instant start = o.getCreatedAt().toInstant(ZoneOffset.UTC).plusSeconds(60);
        Instant end = o.getCreatedAt().toInstant(ZoneOffset.UTC).plusSeconds(minutesToDeliver * 60L);
        when(tracePort.findClientTrace(o.getId(), clientId))
                .thenReturn(List.of(
                        trace(o, "EN_PREPARACION", start),
                        trace(o, "ENTREGADO", end)
                ));
    }
}

