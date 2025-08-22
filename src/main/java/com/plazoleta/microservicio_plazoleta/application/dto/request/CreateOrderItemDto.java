package com.plazoleta.microservicio_plazoleta.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateOrderItemDto {
    private Long dishId;
    private int quantity;
}
