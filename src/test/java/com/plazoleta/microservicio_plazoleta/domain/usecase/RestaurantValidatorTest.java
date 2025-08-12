package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.model.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantValidatorTest {

    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
         restaurant = new Restaurant(
                1L,
                "Restaurante de prueba",
                "Calle falsa 123",
                10L,
                "+573005698325",
                "http://logo.com/logo.png",
                "105655555"
         );
    }

    @Test
    void restaurantIsValid() {
        assertDoesNotThrow(() -> RestaurantValidator.validate(restaurant));
    }

    @Test
    void nameIsNullOrEmpty() {
        restaurant.setName("");
        DomainException ex = assertThrows(DomainException.class,
                () -> RestaurantValidator.validate(restaurant));
        assertTrue(ex.getMessage().contains("Nombre"));
    }

    @Test
    void nitIsNullOrEmpty() {
        restaurant.setNit("");
        DomainException ex = assertThrows(DomainException.class,
                () -> RestaurantValidator.validate(restaurant));
        assertTrue(ex.getMessage().contains("NIT"));
    }

    @Test
    void addressIsNullOrEmpty() {
        restaurant.setAddress("");
        DomainException ex = assertThrows(DomainException.class,
                () -> RestaurantValidator.validate(restaurant));
        assertTrue(ex.getMessage().contains("Dirección"));
    }

    @Test
    void phoneIsNullOrEmpty() {
        restaurant.setPhone("");
        DomainException ex = assertThrows(DomainException.class,
                () -> RestaurantValidator.validate(restaurant));
        assertTrue(ex.getMessage().contains("Teléfono"));
    }

    @Test
    void urlLogoIsNullOrEmpty() {
        restaurant.setUrlLogo("");
        DomainException ex = assertThrows(DomainException.class,
                () -> RestaurantValidator.validate(restaurant));
        assertTrue(ex.getMessage().contains("URL Logo"));
    }

    @Test
    void idOwnerIsNull() {
        restaurant.setIdOwner(null);
        DomainException ex = assertThrows(DomainException.class,
                () -> RestaurantValidator.validate(restaurant));
        assertTrue(ex.getMessage().contains("Id Propietario"));
    }

    @Test
    void nitIsNotNumeric() {
        restaurant.setNit("ABC123");
        DomainException ex = assertThrows(DomainException.class,
                () -> RestaurantValidator.validate(restaurant));
        assertEquals("Nit Invalido", ex.getMessage());
    }

    @Test
    void phoneIsNotNumeric() {
        restaurant.setPhone("ABC123");
        DomainException ex = assertThrows(DomainException.class,
                () -> RestaurantValidator.validate(restaurant));
        assertEquals("Teléfono invalido", ex.getMessage());
    }

    @Test
    void nameWithLettersAndNumbersIsValid() {
        restaurant.setName("Restaurante123");
        assertDoesNotThrow(() -> RestaurantValidator.validate(restaurant));
    }

    @Test
    void phoneMaxLength() {
        restaurant.setPhone("12345678901234");
        DomainException ex = assertThrows(DomainException.class,
                () -> RestaurantValidator.validate(restaurant));
        assertEquals("Teléfono invalido", ex.getMessage());
    }

    @Test
    void nameHasOnlyNumbers() {
        restaurant.setName("12345");
        DomainException ex = assertThrows(DomainException.class,
                () -> RestaurantValidator.validate(restaurant));
        assertEquals("El nombre del restaurante no puede contener solo números", ex.getMessage());
    }

}
