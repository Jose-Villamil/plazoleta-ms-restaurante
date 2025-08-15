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

import static com.plazoleta.microservicio_plazoleta.domain.util.Constantes.*;
import static com.plazoleta.microservicio_plazoleta.domain.util.DomainMessages.*;
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
                "Pizza",
                "Porción de pizza",
                2000,
                "https://imagen.com/pizza.png",
                1L,
                3L
        );
    }

    private void mockAuthenticatedOwner(String roleName, Long ownerId) {
        when(authServicePort.getAuthenticatedUserId()).thenReturn(ownerId);
        when(userPersistencePort.findById(ownerId))
                .thenReturn(Optional.of(new User(ownerId, new Role(1L, roleName, ""))));
        when(restaurantPersistencePort.findRestaurantById(dish.getRestaurantId()))
                .thenReturn(Optional.of(new Restaurant(dish.getRestaurantId(), "Restaurant", "Calle Falsa",
                        ownerId, "123456", "http://logo.png", "123456")));
    }

    private void mockRestaurant(Long restaurantId, Long ownerId) {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setIdOwner(ownerId);
        when(restaurantPersistencePort.findRestaurantById(restaurantId))
                .thenReturn(Optional.of(restaurant));
    }

    @Test
    void nameIsNullOrEmpty() {
        dish.setName("");

        DomainException exception = assertThrows(DomainException.class, () ->
                dishUseCase.saveDish(dish));

        assertEquals(String.format(FIELD_REQUIRED,"Nombre"), exception.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    void descriptionIsNullOrEmpty() {
        dish.setDescription("");

        DomainException exception = assertThrows(DomainException.class, () ->
                dishUseCase.saveDish(dish));

        assertEquals(String.format(FIELD_REQUIRED,"Descripción"), exception.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    void urlImageIsNullOrEmpty() {
        dish.setUrlImage("");

        DomainException exception = assertThrows(DomainException.class, () ->
                dishUseCase.saveDish(dish));

        assertEquals(String.format(FIELD_REQUIRED,"Url Imagen"), exception.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    void categoryIsNullOrEmpty() {
        dish.setCategoryId(null);

        DomainException exception = assertThrows(DomainException.class, () ->
                dishUseCase.saveDish(dish));

        assertEquals(String.format(FIELD_REQUIRED,"Categoría"), exception.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    void restaurantIsNullOrEmpty() {
        dish.setRestaurantId(null);

        DomainException exception = assertThrows(DomainException.class, () ->
                dishUseCase.saveDish(dish));

        assertEquals(String.format(FIELD_REQUIRED,"Restaurante"), exception.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    void positivePriceNumberInt() {
        dish.setPrice(-1);

        DomainException exception = assertThrows(DomainException.class, () ->
                dishUseCase.saveDish(dish));

        assertEquals(String.format(FIELD_INVALID,"Precio"), exception.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    void priceGreaterThanZero() {
        dish.setPrice(0);

        DomainException exception = assertThrows(DomainException.class, () ->
                dishUseCase.saveDish(dish));

        assertEquals(String.format(FIELD_INVALID,"Precio"), exception.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    void restaurantNotFound() {
        mockAuthenticatedOwner(ROLE_OWNER, 1L);
        when(restaurantPersistencePort.findRestaurantById(any())).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class, () -> dishUseCase.saveDish(dish));
        assertEquals(RESTAURANT_NOT_FOUND, ex.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    void userNotOwnerRole() {
        mockAuthenticatedOwner(ROLE_CLIENT, 1L);
        mockRestaurant(1L, 1L);

        DomainException ex = assertThrows(DomainException.class, () -> dishUseCase.saveDish(dish));
        assertTrue(ex.getMessage().contains(ROLE_OWNER));
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    void restaurantDoesNotBelongToOwner() {
        mockAuthenticatedOwner(ROLE_OWNER, 1L);
        mockRestaurant(1L, 99L);

        DomainException ex = assertThrows(DomainException.class, () -> dishUseCase.saveDish(dish));
        assertEquals(NOT_OWNER_RESTAURANT, ex.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    void saveDishWhenValidData() {
        mockAuthenticatedOwner(ROLE_OWNER, 1L);
        mockRestaurant(2L, 1L);

        dishUseCase.saveDish(dish);

        assertTrue(dish.isActive());
        verify(dishPersistencePort).saveDish(dish);
    }

    @Test
    void updateDishWhenOwnerIsPropietario() {
        when(dishPersistencePort.findDishById(1L)).thenReturn(Optional.of(dish));
        mockAuthenticatedOwner(ROLE_OWNER, 5L);

        Dish updateRequest = new Dish();
        updateRequest.setId(1L);
        updateRequest.setDescription("Nueva desc");
        updateRequest.setPrice(2000);

        dishUseCase.updateDish(updateRequest);

        assertEquals("Nueva desc", dish.getDescription());
        assertEquals(2000, dish.getPrice());
        verify(dishPersistencePort).updateDish(dish);
    }

    @Test
    void updateDishNotFound() {
        when(dishPersistencePort.findDishById(99L)).thenReturn(Optional.empty());

        Dish updateRequest = new Dish();
        updateRequest.setId(99L);
        updateRequest.setDescription("Nueva desc");
        updateRequest.setPrice(2000);

        DomainException ex = assertThrows(DomainException.class,
                () -> dishUseCase.updateDish(updateRequest));

        assertEquals(DISH_NOT_FOUND, ex.getMessage());
        verify(dishPersistencePort, never()).updateDish(any());
    }

    @Test
    void updateDishPriceIsInvalid() {
        Dish updateRequest = new Dish();
        updateRequest.setId(1L);
        updateRequest.setDescription("desc");
        updateRequest.setPrice(-100);

        assertThrows(DomainException.class, () -> dishUseCase.updateDish(updateRequest));
        verify(dishPersistencePort, never()).updateDish(any());
    }

    @Test
    void updateDishDescriptionIsNull() {
        Dish updateRequest = new Dish();
        updateRequest.setId(1L);
        updateRequest.setPrice(1500);

        assertThrows(DomainException.class, () -> dishUseCase.updateDish(updateRequest));
        verify(dishPersistencePort, never()).updateDish(any());
    }

    @Test
    void updateDishUserNotOwnerOfRestaurant() {
        when(dishPersistencePort.findDishById(1L)).thenReturn(Optional.of(dish));

        Long ownerId = 99L;
        when(authServicePort.getAuthenticatedUserId()).thenReturn(ownerId);
        when(userPersistencePort.findById(ownerId))
                .thenReturn(Optional.of(new User(ownerId, new Role(1L, ROLE_OWNER, ""))));
        when(restaurantPersistencePort.findRestaurantById(dish.getRestaurantId()))
                .thenReturn(Optional.of(new Restaurant(dish.getRestaurantId(), "Rest", "Addr",
                        77L, "123456", "logo.png", "99999")));

        Dish updateRequest = new Dish();
        updateRequest.setId(1L);
        updateRequest.setDescription("Nueva desc");
        updateRequest.setPrice(2300);

        DomainException ex = assertThrows(DomainException.class,
                () -> dishUseCase.updateDish(updateRequest));

        assertEquals(NOT_OWNER_RESTAURANT, ex.getMessage());
        verify(dishPersistencePort, never()).updateDish(any());
    }
}
