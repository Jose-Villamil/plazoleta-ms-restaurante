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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

class DishUseCaseTest {

    private Dish dish;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;
    @Mock
    private IUserPersistencePort userPersistencePort;
    @Mock
    private IDishPersistencePort  dishPersistencePort;
    @Mock
    private IAuthServicePort authServicePort;
    @InjectMocks
    private DishUseCase dishUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dish = new Dish(
                "Pizza Margarita",
                "Pizza con queso y tomate",
                20000,
                "http://imagen.com/pizza.png",
                1L,
                2L
        );
    }

    private void mockAuthenticatedOwner(String roleName, Long ownerId) {
        when(authServicePort.getAuthenticatedUserId()).thenReturn(ownerId);
        when(userPersistencePort.findById(ownerId))
                .thenReturn(Optional.of(new User(ownerId, new Role(1L, roleName, ""))));
    }

    private void mockRestaurant(Long restaurantId, Long ownerId) {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setIdOwner(ownerId);
        when(restaurantPersistencePort.getRestaurantById(restaurantId))
                .thenReturn(Optional.of(restaurant));
    }

    @Test
    void nameIsNullOrEmpty() {
        dish.setName("");

        DomainException exception = assertThrows(DomainException.class, () ->
                dishUseCase.saveDish(dish));

        assertEquals("El campo Nombre no puede ser nulo o vacío", exception.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    void descriptionIsNullOrEmpty() {
        dish.setDescription("");

        DomainException exception = assertThrows(DomainException.class, () ->
                dishUseCase.saveDish(dish));

        assertEquals("El campo Descripción no puede ser nulo o vacío", exception.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    void urlImageIsNullOrEmpty() {
        dish.setUrlImage("");

        DomainException exception = assertThrows(DomainException.class, () ->
                dishUseCase.saveDish(dish));

        assertEquals("El campo Url Imagen no puede ser nulo o vacío", exception.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    void categoryIsNullOrEmpty() {
        dish.setCategoryId(null);

        DomainException exception = assertThrows(DomainException.class, () ->
                dishUseCase.saveDish(dish));

        assertEquals("El campo Categoría no puede ser nulo o vacío", exception.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    void restaurantIsNullOrEmpty() {
        dish.setRestaurantId(null);

        DomainException exception = assertThrows(DomainException.class, () ->
                dishUseCase.saveDish(dish));

        assertEquals("El campo Restaurante no puede ser nulo o vacío", exception.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    void positivePriceNumberInt() {
        dish.setPrice(-1);

        DomainException exception = assertThrows(DomainException.class, () ->
                dishUseCase.saveDish(dish));

        assertEquals("Precio inválido", exception.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    void priceGreaterThanZero() {
        dish.setPrice(0);

        DomainException exception = assertThrows(DomainException.class, () ->
                dishUseCase.saveDish(dish));

        assertEquals("Precio inválido", exception.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    void restaurantNotFound() {
        mockAuthenticatedOwner("PROPIETARIO", 1L);
        when(restaurantPersistencePort.getRestaurantById(any())).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class, () -> dishUseCase.saveDish(dish));
        assertEquals("No se encontró el restaurante", ex.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    void userNotOwnerRole() {
        mockAuthenticatedOwner("CLIENTE", 1L);
        mockRestaurant(1L, 1L);

        DomainException ex = assertThrows(DomainException.class, () -> dishUseCase.saveDish(dish));
        assertTrue(ex.getMessage().contains("PROPIETARIO"));
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    void restaurantDoesNotBelongToOwner() {
        mockAuthenticatedOwner("PROPIETARIO", 1L);
        mockRestaurant(1L, 99L); // Dueño distinto

        DomainException ex = assertThrows(DomainException.class, () -> dishUseCase.saveDish(dish));
        assertEquals("No eres el propietario del restaurante", ex.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    void saveDishWhenValidData() {
        mockAuthenticatedOwner("PROPIETARIO", 1L);
        mockRestaurant(1L, 1L);

        dishUseCase.saveDish(dish);

        assertTrue(dish.isActive());
        verify(dishPersistencePort).saveDish(dish);
    }

}
