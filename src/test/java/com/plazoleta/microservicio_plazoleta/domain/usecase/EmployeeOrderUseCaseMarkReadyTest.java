package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.api.IAuthServicePort;
import com.plazoleta.microservicio_plazoleta.domain.model.Order;
import com.plazoleta.microservicio_plazoleta.domain.model.OrderStatus;
import com.plazoleta.microservicio_plazoleta.domain.model.Restaurant;
import com.plazoleta.microservicio_plazoleta.domain.model.User;
import com.plazoleta.microservicio_plazoleta.domain.spi.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeOrderUseCaseMarkReadyTest {

    @Mock
    IAuthServicePort auth;
    @Mock
    IRestaurantEmployeePersistencePort empPort;
    @Mock
    IOrderPersistencePort orderPort;
    @Mock
    IUserPersistencePort userPort;
    @Mock
    IRestaurantPersistencePort restPort;
    @Mock
    INotificationOutPort notif;

    @InjectMocks
    EmployeeOrderUseCase useCase;

    @Test
    void markReady_happyPath() {
        Long orderId = 10L;
        Long employeeId = 99L;
        when(auth.getAuthenticatedUserId()).thenReturn(employeeId);

        Order o = new Order();
        o.setId(orderId);
        o.setRestaurantId(5L);
        o.setStatus(OrderStatus.EN_PREPARACION);
        o.setChefId(employeeId);
        o.setClientId(42L);
        o.setPickupPin("123456");
        when(orderPort.findById(orderId)).thenReturn(Optional.of(o));

        when(empPort.findRestaurantIdByEmployeeId(employeeId)).thenReturn(Optional.of(5L));

        var client = new User(); client.setId(42L); client.setPhoneNumber("+573001112233");
        when(userPort.findById(42L)).thenReturn(Optional.of(client));

        var rest = new Restaurant(); rest.setId(5L); rest.setName("Mi Restaurante");
        when(restPort.findRestaurantById(5L)).thenReturn(Optional.of(rest));
        when(auth.getAuthenticatedEmail()).thenReturn("cliente@test.com");
        when(orderPort.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order updated = useCase.markOrderAsReady(orderId);

        assertEquals(OrderStatus.LISTO, updated.getStatus());
        verify(orderPort).save(any(Order.class));
        verify(notif).sendOrderReady(eq(orderId), eq("+573001112233"), eq("123456"), eq(5L), eq("Mi Restaurante"));
    }
}

