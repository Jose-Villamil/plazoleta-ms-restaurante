package com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.client;

import com.plazoleta.microservicio_plazoleta.infrastructure.configuration.security.FeignGlobalConfig;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.dto.UserFeignResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "microservicio-usuarios", url = "${usuarios.service.url}",configuration = FeignGlobalConfig.class)
public interface IUserFeignClient {

    @GetMapping("/api/v1/users/{id}")
    UserFeignResponseDto getUserById(@PathVariable("id") Long id);
}
