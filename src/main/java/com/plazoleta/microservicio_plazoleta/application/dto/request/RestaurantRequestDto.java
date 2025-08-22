package com.plazoleta.microservicio_plazoleta.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RestaurantRequestDto {
    private String name;
    private String address;
    private Long idOwner;
    private String phone;
    private String urlLogo;
    private String nit;
}
