package com.plazoleta.microservicio_plazoleta.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RestaurantListItemResponseDto {
    private String name;
    private String urlLogo;
}
