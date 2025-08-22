package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.api.IAuthServicePort;
import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.spi.IDishPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IRestaurantPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IUserPersistencePort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DishUseCaseListDishTest {

    @Mock
    private IDishPersistencePort dishPersistencePort;
    @InjectMocks
    private DishUseCase dishUseCase;

    @Test
    void throwsWhenRestaurantIdInvalid() {
        assertThrows(
                DomainException.class,
                () -> dishUseCase.listDishesByRestaurant(0L, null, 0, 10)
        );
    }

    @Test
    void delegatesToPort_withSanitizedPageSize() {
        dishUseCase.listDishesByRestaurant(7L, 3L, -1, 0);
        verify(dishPersistencePort)
                .findActiveByRestaurantOrderByNameAsc(7L, 3L, 0, 10);
    }
}
