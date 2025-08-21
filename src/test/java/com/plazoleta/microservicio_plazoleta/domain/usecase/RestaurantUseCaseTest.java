package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.model.Restaurant;
import com.plazoleta.microservicio_plazoleta.domain.model.Role;
import com.plazoleta.microservicio_plazoleta.domain.model.User;
import com.plazoleta.microservicio_plazoleta.domain.spi.IRestaurantPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IUserPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static com.plazoleta.microservicio_plazoleta.domain.util.Constantes.ROLE_OWNER;
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

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    private Restaurant validRestaurant() {
        Restaurant r = new Restaurant();
        r.setName("Restaurante de prueba");
        r.setNit("105655555");
        r.setAddress("Calle falsa 123");
        r.setPhone("+573005698325");
        r.setUrlLogo("http://logo.com/logo.png");
        r.setIdOwner(1L);
        return r;
    }

    private void mockOwnerWithRole(String roleName) {
        User owner = new User(1L, new Role(1L, roleName, ""));
        when(userPersistencePort.findById(1L)).thenReturn(Optional.of(owner));
    }

    @Test
    void SaveRestaurant_WhenValidDataAndOwnerHasCorrectRole() {
        Restaurant r = validRestaurant();
        User owner = new User(1L, new Role(1L, ROLE_OWNER, ""));
        when(userPersistencePort.findById(1L)).thenReturn(Optional.of(owner));

        assertDoesNotThrow(() -> restaurantUseCase.saveRestaurant(r));

        verify(userPersistencePort).findById(1L);
        verify(restaurantPersistencePort).saveRestaurant(r);
    }

    @Test
    void throwException_WhenOwnerDoesNotExist() {
        Restaurant r = validRestaurant();
        when(userPersistencePort.findById(1L)).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class, () -> restaurantUseCase.saveRestaurant(r));

        assertTrue(ex.getMessage().toLowerCase().contains("propietario no existe"));
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    void throwWhenOwnerIsNotPropietario() {
        Restaurant r = validRestaurant();
        User notOwner = new User(1L, new Role(2L, "CLIENTE", ""));
        when(userPersistencePort.findById(1L)).thenReturn(Optional.of(notOwner));

        DomainException ex = assertThrows(DomainException.class, () -> restaurantUseCase.saveRestaurant(r));

        assertTrue(ex.getMessage().toLowerCase().contains("no tienen permiso"));
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }
}
