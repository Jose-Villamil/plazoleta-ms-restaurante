package com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class RoleFeignResponseDto {
    private Long id;
    private String name;
    private String description;
}
