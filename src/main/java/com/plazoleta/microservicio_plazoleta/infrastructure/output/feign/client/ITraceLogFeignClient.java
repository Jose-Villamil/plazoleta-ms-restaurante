package com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.client;

import com.plazoleta.microservicio_plazoleta.infrastructure.configuration.security.FeignLogTraceConfig;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.dto.TraceCreateLogFeignRequestDto;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.dto.TraceLogFeignResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "ms-trazabilidad", url = "${trazabilidad.service.url}",
        configuration = FeignLogTraceConfig.class)
public interface ITraceLogFeignClient {
    @PostMapping()
    void create(@RequestBody TraceCreateLogFeignRequestDto body);

    @GetMapping("/orders/{orderId}/clients/{clientId}")
    List<TraceLogFeignResponseDto> getClientTrace(@PathVariable Long orderId, @PathVariable Long clientId);
}

