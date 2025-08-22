package com.plazoleta.microservicio_plazoleta.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DishListItemResponseDto {
    private Long id;
    private String name;
    private int price;
    private String description;
    private String urlImage;
    private Long categoryId;
}
