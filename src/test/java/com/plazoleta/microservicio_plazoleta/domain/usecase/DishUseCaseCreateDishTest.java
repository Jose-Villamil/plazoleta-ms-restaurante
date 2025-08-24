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
        import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DishUseCaseCreateDishTest{

    @Mock private IDishPersistencePort dishPersistencePort;
    @Mock private IAuthServicePort authServicePort;
    @Mock private IUserPersistencePort userPersistencePort;
    @Mock private IRestaurantPersistencePort restaurantPersistencePort;
    @InjectMocks private DishUseCase dishUseCase;

    private Dish validDish;

    @BeforeEach
    void setUp() {
        validDish = new Dish("Pizza", "Porción de pizza", 2000,
                "https://imagen.com/pizza.png", 1L, 3L);
    }

    private void mockAuthAndUser(Long userId, String roleName) {
        when(authServicePort.getAuthenticatedUserId()).thenReturn(userId);
        when(userPersistencePort.findById(userId))
                .thenReturn(Optional.of(new User(userId, new Role(1L, roleName, ""))));
    }

    private void mockRestaurantOwnedBy(Long restaurantId, Long ownerId) {
        Restaurant r = new Restaurant(restaurantId, "R", "Dir", ownerId, "+57", "logo", "123");
        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(Optional.of(r));
    }

    @Test void should_Fail_When_NameBlank() {
        validDish.setName("");
        DomainException ex = assertThrows(DomainException.class, () -> dishUseCase.saveDish(validDish));
        assertEquals(String.format(FIELD_REQUIRED, "Nombre"), ex.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test void should_Fail_When_DescriptionBlank() {
        validDish.setDescription("");
        DomainException ex = assertThrows(DomainException.class, () -> dishUseCase.saveDish(validDish));
        assertEquals(String.format(FIELD_REQUIRED, "Descripción"), ex.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test void should_Fail_When_UrlImageBlank() {
        validDish.setUrlImage("");
        DomainException ex = assertThrows(DomainException.class, () -> dishUseCase.saveDish(validDish));
        assertEquals(String.format(FIELD_REQUIRED, "Url Imagen"), ex.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test void should_Fail_When_CategoryNull() {
        validDish.setCategoryId(null);
        DomainException ex = assertThrows(DomainException.class, () -> dishUseCase.saveDish(validDish));
        assertEquals(String.format(FIELD_REQUIRED, "Categoría"), ex.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test void should_Fail_When_RestaurantNull() {
        validDish.setRestaurantId(null);
        DomainException ex = assertThrows(DomainException.class, () -> dishUseCase.saveDish(validDish));
        assertEquals(String.format(FIELD_REQUIRED, "Restaurante"), ex.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test void should_Fail_When_PriceNotPositive() {
        validDish.setPrice(0);
        DomainException ex = assertThrows(DomainException.class, () -> dishUseCase.saveDish(validDish));
        assertEquals(String.format(FIELD_INVALID, "Precio"), ex.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    void should_Fail_When_RestaurantNotFound() {
        mockAuthAndUser(1L, ROLE_OWNER);
        when(restaurantPersistencePort.findRestaurantById(validDish.getRestaurantId()))
                .thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class, () -> dishUseCase.saveDish(validDish));
        assertEquals(RESTAURANT_NOT_FOUND, ex.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test void should_Fail_When_UserDoesNotOwnRestaurant() {
        mockAuthAndUser(1L, ROLE_OWNER);
        mockRestaurantOwnedBy(validDish.getRestaurantId(), 99L);

        DomainException ex = assertThrows(DomainException.class, () -> dishUseCase.saveDish(validDish));
        assertEquals(NOT_RESTAURANT_OWNER, ex.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test void should_Save_When_AllOk() {
        mockAuthAndUser(1L, ROLE_OWNER);
        mockRestaurantOwnedBy(validDish.getRestaurantId(), 1L);

        dishUseCase.saveDish(validDish);

        assertTrue(validDish.isActive());
        verify(dishPersistencePort).saveDish(validDish);
    }
}

