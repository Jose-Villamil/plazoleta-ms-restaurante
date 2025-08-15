package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.model.Restaurant;
import com.plazoleta.microservicio_plazoleta.domain.model.Role;
import com.plazoleta.microservicio_plazoleta.domain.model.User;
import com.plazoleta.microservicio_plazoleta.domain.spi.IRestaurantPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IUserPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RestaurantUseCaseTest {
    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;
    @Mock
    private IUserPersistencePort userPersistencePort;
    @InjectMocks
    private RestaurantUseCase restaurantUseCase;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        restaurant = new Restaurant(
                1L,
                "Restaurante de prueba",
                "Calle falsa 123",
                1L,
                "+573005698325",
                "http://logo.com/logo.png",
                "105655555");
    }

    private void mockOwnerWithRole(String roleName) {
        User owner = new User(1L, new Role(1L, roleName, ""));
        when(userPersistencePort.findById(1L)).thenReturn(Optional.of(owner));
    }

    @Test
    void nameIsNullOrEmpty() {
        restaurant.setName("");
        mockOwnerWithRole("ADMINISTRADOR");

        DomainException exception = assertThrows(DomainException.class, () ->
                restaurantUseCase.saveRestaurant(restaurant));

        assertEquals("El campo Nombre no puede ser nulo o vacío", exception.getMessage());
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    void nitIsNullOrEmpty() {
        restaurant.setNit("");
        mockOwnerWithRole("ADMINISTRADOR");

        DomainException exception = assertThrows(DomainException.class, () ->
                restaurantUseCase.saveRestaurant(restaurant));

        assertEquals("El campo Nit no puede ser nulo o vacío", exception.getMessage());
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    void addressIsNullOrEmpty() {
        restaurant.setAddress(null);
        mockOwnerWithRole("ADMINISTRADOR");

        DomainException exception = assertThrows(DomainException.class, () ->
                restaurantUseCase.saveRestaurant(restaurant));

        assertEquals("El campo Dirección no puede ser nulo o vacío", exception.getMessage());
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    void urlLogoIsNullOrEmpty() {
        restaurant.setUrlLogo("  ");
        mockOwnerWithRole("ADMINISTRADOR");

        DomainException exception = assertThrows(DomainException.class, () ->
                restaurantUseCase.saveRestaurant(restaurant));

        assertEquals("El campo Url Logo no puede ser nulo o vacío", exception.getMessage());
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    void idOwnerIsNull() {
        restaurant.setIdOwner(null);
        mockOwnerWithRole("ADMINISTRADOR");

        DomainException exception = assertThrows(DomainException.class, () ->
                restaurantUseCase.saveRestaurant(restaurant));

        assertEquals("El campo Id Propietario no puede ser nulo o vacío", exception.getMessage());
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    void nitIsNotNumeric() {
        restaurant.setNit("ABC123");
        mockOwnerWithRole("ADMINISTRADOR");

        DomainException exception = assertThrows(DomainException.class, () ->
                restaurantUseCase.saveRestaurant(restaurant));

        assertEquals("Nit inválido", exception.getMessage());
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidPhones")
    void invalidPhoneScenarios(String phone, String expectedMessage) {
        restaurant.setPhone(phone);
        mockOwnerWithRole("ADMINISTRADOR");

        DomainException exception = assertThrows(DomainException.class,
                () -> restaurantUseCase.saveRestaurant(restaurant));

        assertEquals(expectedMessage, exception.getMessage());
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    private static Stream<Arguments> provideInvalidPhones() {
        return Stream.of(
                Arguments.of(null, "El campo Teléfono no puede ser nulo o vacío"),
                Arguments.of("", "El campo Teléfono no puede ser nulo o vacío"),
                Arguments.of("ABC123", "Teléfono inválido"),
                Arguments.of("12345678901234", "Teléfono inválido")
        );
    }

    @Test
    void nameIsOnlyNumbers() {
        restaurant.setName("1234567890");
        mockOwnerWithRole("ADMINISTRADOR");

        DomainException exception = assertThrows(DomainException.class, () ->
                restaurantUseCase.saveRestaurant(restaurant)
        );

        assertEquals("El nombre del restaurante no puede contener solo números", exception.getMessage());
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    void throwExceptionWhenNitIsInvalid() {
        restaurant.setNit("ABC123");
        mockOwnerWithRole("ADMINISTRADOR");

        DomainException ex = assertThrows(DomainException.class,
                () -> restaurantUseCase.saveRestaurant(restaurant)
        );

        assertEquals("Nit inválido", ex.getMessage());
        verify(userPersistencePort, never()).findById(any());
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    void SaveRestaurant_WhenValidDataAndOwnerHasCorrectRole() {
        mockOwnerWithRole("PROPIETARIO");

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

        assertEquals("El usuario propietario no existe", exception.getMessage());
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    void throwWhenOwnerIsNotPropietario() {
        User owner = new User(10L, new Role(2L, "CLIENTE", ""));
        when(userPersistencePort.findById(restaurant.getIdOwner())).thenReturn(Optional.of(owner));

        DomainException ex = assertThrows(DomainException.class,
                () -> restaurantUseCase.saveRestaurant(restaurant));

        assertTrue(ex.getMessage().contains("no tienen permiso para realizar esta acción"));
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }
}
