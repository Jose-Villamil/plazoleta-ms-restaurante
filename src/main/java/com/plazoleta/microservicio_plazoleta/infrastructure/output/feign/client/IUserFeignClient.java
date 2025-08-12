package com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.client;

import com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.dto.UserFeignResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "microservicio-usuarios", url = "${usuarios.service.url}")
public interface IUserFeignClient {

    @GetMapping("/api/users/{id}")
    UserFeignResponseDto getUserById(@PathVariable("id") Long id);
}
