package com.plazoleta.microservicio_plazoleta.infrastructure.configuration;

import com.plazoleta.microservicio_plazoleta.domain.api.IRestaurantServicePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IRestaurantPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IUserPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.usecase.RestaurantUseCase;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.adapter.UserFeignAdapter;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.client.IUserFeignClient;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.mapper.IUserFeignMapper;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.adapter.RestaurantJpaAdapter;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.mapper.IRestaurantEntityMapper;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;
    private final IUserFeignClient userFeignClient;
    private final IUserFeignMapper userFeignMapper;

    @Bean
    public IRestaurantServicePort restaurantService() {
        return new RestaurantUseCase(restaurantPersistencePort(),userPersistencePort());
    }

    @Bean
    public IRestaurantPersistencePort restaurantPersistencePort(){
        return new RestaurantJpaAdapter(restaurantRepository, restaurantEntityMapper);
    }

    @Bean
    public IUserPersistencePort userPersistencePort() {
        return new UserFeignAdapter(userFeignClient, userFeignMapper);
    }

}
