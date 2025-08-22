package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.api.IAuthServicePort;
import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.model.Order;
import com.plazoleta.microservicio_plazoleta.domain.model.OrderStatus;
import com.plazoleta.microservicio_plazoleta.domain.spi.IRestaurantEmployeePersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IOrderPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.util.PageResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeOrderUseCaseTest {

    @Mock private IAuthServicePort authServicePort;
    @Mock private IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort;
    @Mock private IOrderPersistencePort orderPersistencePort;

    @InjectMocks
    private EmployeeOrderUseCase useCase;

    @Test
    void listOrdersByStatus_whenStatusIsNull_throwsDomainException() {
        DomainException ex = assertThrows(DomainException.class,
                () -> useCase.listOrdersByStatus(null, 0, 10));
        assertTrue(ex.getMessage().toLowerCase().contains("estado"));
        verifyNoInteractions(authServicePort, restaurantEmployeePersistencePort, orderPersistencePort);
    }

    @Test
    void listOrdersByStatus_whenEmployeeHasNoRestaurant_throwsDomainException() {
        when(authServicePort.getAuthenticatedUserId()).thenReturn(77L);
        when(restaurantEmployeePersistencePort.findRestaurantIdByEmployeeId(77L))
                .thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class,
                () -> useCase.listOrdersByStatus(OrderStatus.PENDIENTE, 0, 10));
        assertTrue(ex.getMessage().toLowerCase().contains("no tiene restaurante"));
        verify(authServicePort).getAuthenticatedUserId();
        verify(restaurantEmployeePersistencePort).findRestaurantIdByEmployeeId(77L);
        verifyNoInteractions(orderPersistencePort);
    }

    @Test
    void listOrdersByStatus_normalizesPagination_andDelegatesToRepository() {
        when(authServicePort.getAuthenticatedUserId()).thenReturn(5L);
        when(restaurantEmployeePersistencePort.findRestaurantIdByEmployeeId(5L))
                .thenReturn(Optional.of(9L));

        PageResult<Order> pageResult = new PageResult<>(
                Collections.emptyList(), /* items */
                0,                       /* page (esperado tras normalizar) */
                10,                      /* size (esperado tras normalizar) */
                0,                       /* totalElements */
                0                        /* totalPages */
        );

        when(orderPersistencePort.findByRestaurantAndStatus(9L, OrderStatus.PENDIENTE, 0, 10))
                .thenReturn(pageResult);

        PageResult<Order> out = useCase.listOrdersByStatus(OrderStatus.PENDIENTE, -3, 0);

        assertNotNull(out);
        assertEquals(0, out.getPage());
        assertEquals(10, out.getSize());
        assertEquals(0, out.getTotalElements());

        verify(authServicePort).getAuthenticatedUserId();
        verify(restaurantEmployeePersistencePort).findRestaurantIdByEmployeeId(5L);
        verify(orderPersistencePort).findByRestaurantAndStatus(9L, OrderStatus.PENDIENTE, 0, 10);
        verifyNoMoreInteractions(orderPersistencePort);
    }

    @Test
    void listOrdersByStatus_happyPath_returnsPageResult() {
        when(authServicePort.getAuthenticatedUserId()).thenReturn(11L);
        when(restaurantEmployeePersistencePort.findRestaurantIdByEmployeeId(11L))
                .thenReturn(Optional.of(22L));

        PageResult<Order> pageResult = new PageResult<>(
                Collections.emptyList(), 1, 5, 0, 0
        );
        when(orderPersistencePort.findByRestaurantAndStatus(22L, OrderStatus.LISTO, 1, 5))
                .thenReturn(pageResult);

        PageResult<Order> out = useCase.listOrdersByStatus(OrderStatus.LISTO, 1, 5);

        assertSame(pageResult, out);
        verify(orderPersistencePort).findByRestaurantAndStatus(22L, OrderStatus.LISTO, 1, 5);
    }
}

