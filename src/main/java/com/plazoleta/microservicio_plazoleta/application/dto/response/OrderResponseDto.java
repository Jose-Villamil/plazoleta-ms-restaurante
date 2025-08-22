package com.plazoleta.microservicio_plazoleta.application.dto.response;

import com.plazoleta.microservicio_plazoleta.domain.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderResponseDto {
    private Long id;
    private Long clientId;
    private Long restaurantId;
    private Long chefId;
    private LocalDateTime createdAt;
    private OrderStatus status;
    private String pickupPin;
    private List<OrderItemResponseDto> items;
}
