package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.model.Restaurant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
        import static com.plazoleta.microservicio_plazoleta.domain.usecase.ValidatorUseCase.*;
        import static org.junit.jupiter.api.Assertions.*;

class RestaurantUseCaseValidatorTest {

    private Restaurant validRestaurant() {
        Restaurant r = new Restaurant();
        r.setName("Restaurante de prueba");
        r.setNit("123456789");
        r.setAddress("Calle falsa 123");
        r.setPhone("+573005698325");
        r.setUrlLogo("http://logo.com/img.png");
        r.setIdOwner(1L);
        return r;
    }
    @Test
    void nameIsNullOrEmpty() {
        Restaurant r = validRestaurant();
        r.setName("");
        DomainException ex = assertThrows(DomainException.class, () -> validateRestaurant(r));
        assertTrue(ex.getMessage().contains("Nombre"));
    }

    @Test
    void nitIsNullOrEmpty() {
        Restaurant r = validRestaurant();
        r.setNit("");
        DomainException ex = assertThrows(DomainException.class, () -> validateRestaurant(r));
        assertTrue(ex.getMessage().contains("Nit"));
    }

    @Test
    void addressIsNullOrEmpty() {
        Restaurant r = validRestaurant();
        r.setAddress(null);
        DomainException ex = assertThrows(DomainException.class, () -> validateRestaurant(r));
        assertTrue(ex.getMessage().contains("Dirección"));
    }

    @ParameterizedTest
    @NullSource @ValueSource(strings = {"", " "})
    void whenPhoneBlank(String value) {
        Restaurant r = validRestaurant(); r.setPhone(value);
        DomainException ex = assertThrows(DomainException.class, () -> validateRestaurant(r));
        assertTrue(ex.getMessage().contains("Teléfono"));
    }

    @ParameterizedTest
    @NullSource @ValueSource(strings = {"", " "})
    void whenUrlLogoBlank(String value) {
        Restaurant r = validRestaurant(); r.setUrlLogo(value);
        DomainException ex = assertThrows(DomainException.class, () -> validateRestaurant(r));
        assertTrue(ex.getMessage().contains("Url Logo"));
    }

    @Test
    void whenOwnerIdNull() {
        Restaurant r = validRestaurant(); r.setIdOwner(null);
        DomainException ex = assertThrows(DomainException.class, () -> validateRestaurant(r));
        assertTrue(ex.getMessage().contains("Id Propietario"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ABC123", "123-456", "12 34"})
    void whenNitNotNumeric(String nit) {
        Restaurant r = validRestaurant(); r.setNit(nit);
        DomainException ex = assertThrows(DomainException.class, () -> validateRestaurant(r));
        assertTrue(ex.getMessage().contains("Nit inválido"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ABC123", "12345678901234"}) // >13
    void whenPhoneInvalid(String phone) {
        Restaurant r = validRestaurant(); r.setPhone(phone);
        DomainException ex = assertThrows(DomainException.class, () -> validateRestaurant(r));
        assertTrue(ex.getMessage().contains("Teléfono inválido"));
    }

    @Test
    void whenNameOnlyNumbers() {
        Restaurant r = validRestaurant(); r.setName("123456");
        DomainException ex = assertThrows(DomainException.class, () -> validateRestaurant(r));
        assertTrue(ex.getMessage().contains("no puede contener solo números"));
    }

    @Test
    void shouldPass_whenAllOk() {
        assertDoesNotThrow(() -> validateRestaurant(validRestaurant()));
    }
}
