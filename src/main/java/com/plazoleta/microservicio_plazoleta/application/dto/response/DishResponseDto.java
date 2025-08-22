package com.plazoleta.microservicio_plazoleta.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DishResponseDto {
    private Long id;
    private String name;
    private int price;
    private String description;
    private String urlImage;
    private Long restaurantId;
    private Long categoryId;
    private boolean active;
}
