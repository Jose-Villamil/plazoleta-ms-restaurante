package com.plazoleta.microservicio_plazoleta.domain.spi;

import com.plazoleta.microservicio_plazoleta.domain.model.Dish;

public interface IDishPersistencePort {
    void saveDish(Dish dish);
}
