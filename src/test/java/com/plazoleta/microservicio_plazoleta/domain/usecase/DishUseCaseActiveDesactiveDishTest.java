package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.api.IAuthServicePort;
import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.model.Dish;
import com.plazoleta.microservicio_plazoleta.domain.model.Restaurant;
import com.plazoleta.microservicio_plazoleta.domain.model.Role;
import com.plazoleta.microservicio_plazoleta.domain.model.User;
import com.plazoleta.microservicio_plazoleta.domain.spi.IDishPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IRestaurantPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IUserPersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.plazoleta.microservicio_plazoleta.domain.util.Constantes.ROLE_CLIENT;
import static com.plazoleta.microservicio_plazoleta.domain.util.Constantes.ROLE_OWNER;
import static com.plazoleta.microservicio_plazoleta.domain.util.DomainMessages.*;
        import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DishUseCaseActiveDesactiveDishTest {

    @Mock private IDishPersistencePort dishPersistencePort;
    @Mock private IAuthServicePort authServicePort;
    @Mock private IUserPersistencePort userPersistencePort;
    @Mock private IRestaurantPersistencePort restaurantPersistencePort;
    @InjectMocks private DishUseCase dishUseCase;

    private Dish dish(long id, long restaurantId) {
        Dish d = new Dish("P", "D", 1000, "url", restaurantId, 1L);
        d.setId(id);
        return d;
    }

    private void mockOwner(Long ownerId) {
        when(authServicePort.getAuthenticatedUserId()).thenReturn(ownerId);
        when(userPersistencePort.findById(ownerId))
                .thenReturn(Optional.of(new User(ownerId, new Role(1L, ROLE_OWNER, ""))));
    }

    private void mockRestaurantOwner(Long restaurantId, Long ownerId) {
        Restaurant r = new Restaurant(restaurantId, "R", "Dir", ownerId, "+57", "logo", "123");
        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(Optional.of(r));
    }

    @Test
    void should_Throw_When_IdNullOrNonPositive() {
        DomainException ex1 = assertThrows(DomainException.class, () -> dishUseCase.setDishActive(null, true));
        assertTrue(ex1.getMessage().toLowerCase().contains("id del plato"));

        DomainException ex2 = assertThrows(DomainException.class, () -> dishUseCase.setDishActive(0L, false));
        assertTrue(ex2.getMessage().toLowerCase().contains("id del plato"));

        verify(dishPersistencePort, never()).updateDish(any());
    }

    @Test
    void should_Throw_When_DishNotFound() {
        when(dishPersistencePort.findDishById(999L)).thenReturn(Optional.empty());
        DomainException ex = assertThrows(DomainException.class, () -> dishUseCase.setDishActive(999L, true));
        assertEquals(DISH_NOT_FOUND, ex.getMessage());
        verify(dishPersistencePort, never()).updateDish(any());
    }

    @Test
    void should_Throw_When_CallerIsNotOwner() {
        Dish d = dish(55L, 10L);
        when(dishPersistencePort.findDishById(55L)).thenReturn(Optional.of(d));

        Long uid = 7L;
        when(authServicePort.getAuthenticatedUserId()).thenReturn(uid);
        when(userPersistencePort.findById(uid))
                .thenReturn(Optional.of(new User(uid, new Role(1L, ROLE_CLIENT, ""))));

        DomainException ex = assertThrows(DomainException.class, () -> dishUseCase.setDishActive(55L, true));
        assertTrue(ex.getMessage().contains(ROLE_CLIENT));
        verify(dishPersistencePort, never()).updateDish(any());
    }

    @Test
    void should_Throw_When_OwnerDoesNotOwnRestaurant() {
        Dish d = dish(55L, 10L);
        when(dishPersistencePort.findDishById(55L)).thenReturn(Optional.of(d));

        mockOwner(5L);
        mockRestaurantOwner(10L, 99L);

        DomainException ex = assertThrows(DomainException.class, () -> dishUseCase.setDishActive(55L, false));
        assertEquals(NOT_OWNER_RESTAURANT, ex.getMessage());
        verify(dishPersistencePort, never()).updateDish(any());
    }

    @Test
    void should_EnableDish_When_OwnerOwnsRestaurant() {
        Dish d = dish(66L, 10L);
        when(dishPersistencePort.findDishById(66L)).thenReturn(Optional.of(d));

        mockOwner(8L);
        mockRestaurantOwner(10L, 8L);

        dishUseCase.setDishActive(66L, true);

        assertTrue(d.isActive());
        verify(dishPersistencePort).updateDish(d);
    }

    @Test
    void should_DisableDish_When_OwnerOwnsRestaurant() {
        Dish d = dish(77L, 10L); d.setActive(true);
        when(dishPersistencePort.findDishById(77L)).thenReturn(Optional.of(d));

        mockOwner(8L);
        mockRestaurantOwner(10L, 8L);

        dishUseCase.setDishActive(77L, false);

        assertFalse(d.isActive());
        verify(dishPersistencePort).updateDish(d);
    }
}
