package com.plazoleta.microservicio_plazoleta.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DishRequestDto {
    private String name;
    private String description;
    private int price;
    private String urlImage;
    private Long restaurantId;
    private Long categoryId;
}
