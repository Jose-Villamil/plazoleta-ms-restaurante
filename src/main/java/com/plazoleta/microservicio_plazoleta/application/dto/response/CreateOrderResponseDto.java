package com.plazoleta.microservicio_plazoleta.application.dto.response;

import com.plazoleta.microservicio_plazoleta.domain.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateOrderResponseDto {
    private Long orderId;
    private OrderStatus status;
    private String pickupPin;
}
