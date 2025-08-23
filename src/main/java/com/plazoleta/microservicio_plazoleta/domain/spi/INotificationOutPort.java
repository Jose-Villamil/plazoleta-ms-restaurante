package com.plazoleta.microservicio_plazoleta.domain.spi;

public interface INotificationOutPort {
    void sendOrderReady(Long orderId, String clientPhone, String pickupPin, Long restaurantId, String restaurantName);
}
