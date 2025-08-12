package com.plazoleta.microservicio_plazoleta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.plazoleta.microservicio_plazoleta.infrastructure.output.feign")
public class MicroservicioPlazoletaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicioPlazoletaApplication.class, args);
	}

}
