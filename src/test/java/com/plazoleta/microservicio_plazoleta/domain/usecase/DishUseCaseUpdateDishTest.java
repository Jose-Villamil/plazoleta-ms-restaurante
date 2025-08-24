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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.plazoleta.microservicio_plazoleta.domain.util.Constantes.ROLE_OWNER;
import static com.plazoleta.microservicio_plazoleta.domain.util.DomainMessages.*;
        import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DishUseCaseUpdateDishTest {

    @Mock private IDishPersistencePort dishPersistencePort;
    @Mock private IAuthServicePort authServicePort;
    @Mock private IUserPersistencePort userPersistencePort;
    @Mock private IRestaurantPersistencePort restaurantPersistencePort;
    @InjectMocks private DishUseCase dishUseCase;

    private Dish existing;

    @BeforeEach
    void setUp() {
        existing = new Dish("Pizza", "Desc", 2000, "url", 10L, 3L);
        existing.setId(1L);
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
    void should_Throw_When_DishNotFound() {
        when(dishPersistencePort.findDishById(99L)).thenReturn(Optional.empty());

        Dish req = new Dish(); req.setId(99L); req.setDescription("Nueva"); req.setPrice(1000);

        DomainException ex = assertThrows(DomainException.class, () -> dishUseCase.updateDish(req));
        assertEquals(DISH_NOT_FOUND, ex.getMessage());
        verify(dishPersistencePort, never()).updateDish(any());
    }

    @Test
    void should_Throw_When_PriceInvalid() {
        Dish req = new Dish(); req.setId(1L); req.setDescription("Nueva"); req.setPrice(-1);
        assertThrows(DomainException.class, () -> dishUseCase.updateDish(req));
        verify(dishPersistencePort, never()).updateDish(any());
    }

    @Test
    void should_Throw_When_DescriptionNull() {
        Dish req = new Dish(); req.setId(1L); req.setPrice(1500);
        assertThrows(DomainException.class, () -> dishUseCase.updateDish(req));
        verify(dishPersistencePort, never()).updateDish(any());
    }

    @Test
    void should_Throw_When_UserDoesNotOwnRestaurant() {
        when(dishPersistencePort.findDishById(1L)).thenReturn(Optional.of(existing));
        mockOwner(5L);
        mockRestaurantOwner(existing.getRestaurantId(), 99L);

        Dish req = new Dish(); req.setId(1L); req.setDescription("Nueva"); req.setPrice(2300);

        DomainException ex = assertThrows(DomainException.class, () -> dishUseCase.updateDish(req));
        assertEquals(NOT_RESTAURANT_OWNER, ex.getMessage());
        verify(dishPersistencePort, never()).updateDish(any());
    }

    @Test
    void should_Update_When_OwnerAndValid() {
        when(dishPersistencePort.findDishById(1L)).thenReturn(Optional.of(existing));
        mockOwner(5L);
        mockRestaurantOwner(existing.getRestaurantId(), 5L);

        Dish req = new Dish(); req.setId(1L); req.setDescription("Nueva desc"); req.setPrice(2100);

        dishUseCase.updateDish(req);

        assertEquals("Nueva desc", existing.getDescription());
        assertEquals(2100, existing.getPrice());
        verify(dishPersistencePort).updateDish(existing);
    }
}

