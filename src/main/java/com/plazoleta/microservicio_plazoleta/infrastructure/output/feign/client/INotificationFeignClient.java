package com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(
        name = "ms-mensajeria",
        url = "${mensajeria.service.url}"
)
public interface INotificationFeignClient {

    @PostMapping("/order-ready")
    void sendOrderReady(@RequestHeader("X-API-KEY") String apiKey,
                        @RequestBody Map<String, Object> body);
}

