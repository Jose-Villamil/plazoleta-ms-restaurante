package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.model.*;
import com.plazoleta.microservicio_plazoleta.domain.spi.IDishPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IOrderPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IRestaurantPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.api.IAuthServicePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static com.plazoleta.microservicio_plazoleta.domain.util.DomainMessages.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderUseCaseTest {

    @Mock private IOrderPersistencePort orderPersistencePort;
    @Mock private IAuthServicePort authServicePort;
    @Mock private IRestaurantPersistencePort restaurantPersistencePort;
    @Mock private IDishPersistencePort dishPersistencePort;

    @InjectMocks
    private OrderUseCase useCase;

    private static OrderItem item(Long dishId, int qty) {
        OrderItem it = new OrderItem();
        it.setDishId(dishId);
        it.setQuantity(qty);
        return it;
    }

    private static Order order(Long restaurantId, OrderItem... items) {
        Order o = new Order();
        o.setRestaurantId(restaurantId);
        o.setItems(Arrays.asList(items));
        return o;
    }

    private static Dish activeDish(Long id, Long restaurantId) {
        Dish d = new Dish();
        d.setId(id);
        d.setRestaurantId(restaurantId);
        d.setActive(true);
        d.setName("Dish " + id);
        d.setDescription("Desc");
        d.setPrice(1000);
        return d;
    }

    private static final Set<OrderStatus> IN_PROGRESS = Set.of(
            OrderStatus.PENDIENTE, OrderStatus.EN_PREPARACION, OrderStatus.LISTO
    );


    @Test
    void saveOrder_whenOrderIsNullOrRestaurantMissing_throws() {
        when(authServicePort.getAuthenticatedUserId()).thenReturn(10L);

        DomainException ex1 = assertThrows(DomainException.class, () -> useCase.saveOrder(null));
        assertTrue(ex1.getMessage().toLowerCase().contains("restaurante"));

        Order o = new Order();
        o.setRestaurantId(null);
        o.setItems(List.of(item(1L, 1)));
        DomainException ex2 = assertThrows(DomainException.class, () -> useCase.saveOrder(o));
        assertTrue(ex2.getMessage().toLowerCase().contains("restaurante"));

        verifyNoInteractions(restaurantPersistencePort, dishPersistencePort, orderPersistencePort);
    }

    @Test
    void saveOrder_whenNoItems_throws() {
        when(authServicePort.getAuthenticatedUserId()).thenReturn(10L);
        Order o = order(7L /* restaurantId */);
        o.setItems(Collections.emptyList());

        DomainException ex = assertThrows(DomainException.class, () -> useCase.saveOrder(o));
        assertTrue(ex.getMessage().toLowerCase().contains("al menos un plato"));
        verifyNoInteractions(restaurantPersistencePort, dishPersistencePort, orderPersistencePort);
    }

    @Test
    void saveOrder_whenClientHasOpenOrder_throws() {
        when(authServicePort.getAuthenticatedUserId()).thenReturn(10L);
        when(orderPersistencePort.existsByClientAndStatuses(10L,IN_PROGRESS)).thenReturn(true);

        Order o = order(7L, item(1L, 1));

        DomainException ex = assertThrows(DomainException.class, () -> useCase.saveOrder(o));
        assertTrue(ex.getMessage().toLowerCase().contains("proceso"));
        verify(orderPersistencePort).existsByClientAndStatuses(10L,IN_PROGRESS);
        verifyNoMoreInteractions(orderPersistencePort);
        verifyNoInteractions(restaurantPersistencePort, dishPersistencePort);
    }

    @Test
    void saveOrder_whenRestaurantNotFound_throws() {
        when(authServicePort.getAuthenticatedUserId()).thenReturn(10L);
        when(orderPersistencePort.existsByClientAndStatuses(10L,IN_PROGRESS)).thenReturn(false);
        when(restaurantPersistencePort.findRestaurantById(7L)).thenReturn(Optional.empty());

        Order o = order(7L, item(1L, 1));

        DomainException ex = assertThrows(DomainException.class, () -> useCase.saveOrder(o));
        assertEquals(RESTAURANT_NOT_FOUND, ex.getMessage());

        verify(restaurantPersistencePort).findRestaurantById(7L);
        verifyNoInteractions(dishPersistencePort);
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    void saveOrder_whenItemDishIdNullOrQtyInvalid_throws() {
        when(authServicePort.getAuthenticatedUserId()).thenReturn(10L);
        when(orderPersistencePort.existsByClientAndStatuses(10L,IN_PROGRESS)).thenReturn(false);
        when(restaurantPersistencePort.findRestaurantById(7L)).thenReturn(Optional.of(new Restaurant()));

        Order o1 = order(7L, item(null, 1));
        DomainException ex1 = assertThrows(DomainException.class, () -> useCase.saveOrder(o1));
        assertTrue(ex1.getMessage().toLowerCase().contains("cantidad"));

        Order o2 = order(7L, item(1L, 0));
        DomainException ex2 = assertThrows(DomainException.class, () -> useCase.saveOrder(o2));
        assertTrue(ex2.getMessage().toLowerCase().contains("cantidad"));

        verifyNoInteractions(dishPersistencePort);
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    void saveOrder_whenDuplicateDishes_throws() {
        when(authServicePort.getAuthenticatedUserId()).thenReturn(10L);
        when(orderPersistencePort.existsByClientAndStatuses(10L,IN_PROGRESS)).thenReturn(false);
        when(restaurantPersistencePort.findRestaurantById(7L)).thenReturn(Optional.of(new Restaurant()));
        Dish d = activeDish(2L, 7L);
        when(dishPersistencePort.findDishById(2L)).thenReturn(Optional.of(d));

        Order o = order(7L, item(2L, 1), item(2L, 3));

        DomainException ex = assertThrows(DomainException.class, () -> useCase.saveOrder(o));
        assertTrue(ex.getMessage().toLowerCase().contains("repetido"));
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    void saveOrder_whenDishNotFound_throws() {
        when(authServicePort.getAuthenticatedUserId()).thenReturn(10L);
        when(orderPersistencePort.existsByClientAndStatuses(10L,IN_PROGRESS)).thenReturn(false);
        when(restaurantPersistencePort.findRestaurantById(7L)).thenReturn(Optional.of(new Restaurant()));
        when(dishPersistencePort.findDishById(5L)).thenReturn(Optional.empty());

        Order o = order(7L, item(5L, 1));

        DomainException ex = assertThrows(DomainException.class, () -> useCase.saveOrder(o));
        assertEquals(DISH_NOT_FOUND, ex.getMessage());

        verify(dishPersistencePort).findDishById(5L);
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    void saveOrder_whenDishInactive_throws() {
        when(authServicePort.getAuthenticatedUserId()).thenReturn(10L);
        when(orderPersistencePort.existsByClientAndStatuses(10L,IN_PROGRESS)).thenReturn(false);
        when(restaurantPersistencePort.findRestaurantById(7L)).thenReturn(Optional.of(new Restaurant()));

        Dish inactive = activeDish(3L, 7L);
        inactive.setActive(false);
        when(dishPersistencePort.findDishById(3L)).thenReturn(Optional.of(inactive));

        Order o = order(7L, item(3L, 1));

        DomainException ex = assertThrows(DomainException.class, () -> useCase.saveOrder(o));
        assertTrue(ex.getMessage().toLowerCase().contains("no está activo"));

        verify(dishPersistencePort).findDishById(3L);
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    void saveOrder_whenDishFromAnotherRestaurant_throws() {
        when(authServicePort.getAuthenticatedUserId()).thenReturn(10L);
        when(orderPersistencePort.existsByClientAndStatuses(10L,IN_PROGRESS)).thenReturn(false);
        when(restaurantPersistencePort.findRestaurantById(7L)).thenReturn(Optional.of(new Restaurant()));

        Dish d = activeDish(4L, 9L);
        when(dishPersistencePort.findDishById(4L)).thenReturn(Optional.of(d));

        Order o = order(7L, item(4L, 2));

        DomainException ex = assertThrows(DomainException.class, () -> useCase.saveOrder(o));
        assertTrue(ex.getMessage().toLowerCase().contains("mismo restaurante"));

        verify(dishPersistencePort).findDishById(4L);
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    void saveOrder_happyPath_persistsAndReturnsSavedOrder() {
        Long clientId = 21L;
        Long restaurantId = 7L;

        when(authServicePort.getAuthenticatedUserId()).thenReturn(clientId);
        when(orderPersistencePort.existsByClientAndStatuses(eq(clientId), anySet()))
                .thenReturn(false);

        Restaurant r = new Restaurant();
        r.setId(restaurantId);
        when(restaurantPersistencePort.findRestaurantById(restaurantId))
                .thenReturn(Optional.of(r));

        when(dishPersistencePort.findDishById(10L)).thenReturn(Optional.of(activeDish(10L, restaurantId)));
        when(dishPersistencePort.findDishById(15L)).thenReturn(Optional.of(activeDish(15L, restaurantId)));

        Order input = order(restaurantId, item(10L, 2), item(15L, 1));

        when(orderPersistencePort.save(any(Order.class)))
                .thenAnswer(inv -> {
                    Order arg = inv.getArgument(0);
                    arg.setId(100L);
                    return arg;
                });

        Order out = useCase.saveOrder(input);

        assertNotNull(out);
        assertEquals(100L, out.getId());
        assertEquals(OrderStatus.PENDIENTE, out.getStatus());
        assertEquals(clientId, out.getClientId());
        assertEquals(restaurantId, out.getRestaurantId());
        assertNotNull(out.getItems());
        assertEquals(2, out.getItems().size());

        verify(orderPersistencePort).save(any(Order.class));
    }


}

