package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.model.Restaurant;
import com.plazoleta.microservicio_plazoleta.domain.spi.IRestaurantPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IUserPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.util.PageResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantUseCaseListRestaurantTest {

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;
    @Mock
    private IUserPersistencePort userPersistencePort;
    @InjectMocks
    private RestaurantUseCase restaurantUseCase;

    @Test
    void pageByNameAsc_withOnlyNameAndLogoInResponse() {
        var r1 = new Restaurant();
        r1.setName("A - Arepas");
        r1.setUrlLogo("a.png");
        var r2 = new Restaurant();
        r2.setName("B - Burgers");
        r2.setUrlLogo("b.png");

        var page = new PageResult<>(java.util.List.of(r1, r2), 0, 2, 10, 5);
        when(restaurantPersistencePort.findAllOrderByNameAsc(0,2)).thenReturn(page);

        var result = restaurantUseCase.listRestaurants(0,2);

        assertEquals(2, result.getItems().size());
        assertEquals("A - Arepas", result.getItems().get(0).getName());
        assertEquals("B - Burgers", result.getItems().get(1).getName());
        assertEquals(0, result.getPage());
        assertEquals(2, result.getSize());
        assertEquals(10, result.getTotalElements());
        assertEquals(5, result.getTotalPages());

        verify(restaurantPersistencePort).findAllOrderByNameAsc(0,2);
    }

    @Test
    void sanitizesInvalidPageAndSize() {
        restaurantUseCase.listRestaurants(-1, 0);
        verify(restaurantPersistencePort).findAllOrderByNameAsc(0,10);
    }
}
