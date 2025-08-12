package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.model.Restaurant;
import com.plazoleta.microservicio_plazoleta.domain.model.Role;
import com.plazoleta.microservicio_plazoleta.domain.model.User;
import com.plazoleta.microservicio_plazoleta.domain.spi.IRestaurantPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IUserPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RestaurantUseCaseTest {

    private IRestaurantPersistencePort restaurantPersistencePort;
    private IUserPersistencePort userPersistencePort;
    private RestaurantUseCase restaurantUseCase;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        restaurantPersistencePort = mock(IRestaurantPersistencePort.class);
        userPersistencePort = mock(IUserPersistencePort.class);
        restaurantUseCase = new RestaurantUseCase(restaurantPersistencePort, userPersistencePort);
        restaurant = new Restaurant(
                1L,
                "Restaurante de prueba",
                "Calle falsa 123",
                1L,
                "+573005698325",
                "http://logo.com/logo.png",
                "105655555");
    }

    @Test
    void nameIsOnlyNumbers() {
        restaurant.setName("1234567890");
        User owner = new User(1L, new Role(1L, "ADMINISTRADOR",""));
        when(userPersistencePort.findById(1L)).thenReturn(Optional.of(owner));

        DomainException exception = assertThrows(DomainException.class, () ->
                restaurantUseCase.saveRestaurant(restaurant)
        );

        assertEquals("El nombre del restaurante no puede contener solo números", exception.getMessage());
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    void throwExceptionWhenNitIsInvalid() {
        restaurant.setNit("ABC123");

        DomainException ex = assertThrows(DomainException.class,
                () -> restaurantUseCase.saveRestaurant(restaurant)
        );

        assertEquals("Nit Invalido", ex.getMessage());
        verify(userPersistencePort, never()).findById(any());
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    void SaveRestaurant_WhenValidDataAndOwnerHasCorrectRole() {
        User owner = new User(1L, new Role(1L, "PROPIETARIO", ""));
        when(userPersistencePort.findById(1L)).thenReturn(Optional.of(owner));

        restaurantUseCase.saveRestaurant(restaurant);

        verify(restaurantPersistencePort).saveRestaurant(restaurant);
    }

    @Test
    void throwException_WhenOwnerDoesNotExist() {
        restaurant.setIdOwner(99L);
        when(userPersistencePort.findById(99L)).thenReturn(Optional.empty());

        DomainException exception = assertThrows(DomainException.class, () ->
                restaurantUseCase.saveRestaurant(restaurant)
        );

        assertEquals("El propietario no existe", exception.getMessage());
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    void throwWhenOwnerIsNotPropietario() {
        User owner = new User(10L, new Role(2L, "CLIENTE", ""));
        when(userPersistencePort.findById(restaurant.getIdOwner())).thenReturn(Optional.of(owner));

        DomainException ex = assertThrows(DomainException.class,
                () -> restaurantUseCase.saveRestaurant(restaurant));

        assertEquals("El usuario no tiene el rol PROPIETARIO", ex.getMessage());
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }
}
