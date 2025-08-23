package com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.adapter;

import com.plazoleta.microservicio_plazoleta.domain.spi.INotificationOutPort;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.client.INotificationFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@RequiredArgsConstructor
public class NotificationFeignAdapter implements INotificationOutPort {

    private final INotificationFeignClient client;

    @Value("${mensajeria.apikey}")
    private String apiKey;

    @Override
    public void sendOrderReady(Long orderId, String clientPhone, String pickupPin, Long restaurantId, String restaurantName) {
        Map<String, Object> body = Map.of(
                "orderId", orderId,
                "clientPhone", clientPhone,
                "pickupPin", pickupPin,
                "restaurantId", restaurantId,
                "restaurantName", restaurantName
        );
        client.sendOrderReady(apiKey, body);
    }
}

