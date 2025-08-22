package com.plazoleta.microservicio_plazoleta.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class RestaurantEmployeeRequestDto {
    Long restaurantId;
    Long employeeId;
}
