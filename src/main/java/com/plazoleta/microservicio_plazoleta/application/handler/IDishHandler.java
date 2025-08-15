package com.plazoleta.microservicio_plazoleta.application.handler;

import com.plazoleta.microservicio_plazoleta.application.dto.request.DishRequestDto;

public interface IDishHandler {
    void saveDish(DishRequestDto dishRequestDto);
}
