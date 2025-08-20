package com.plazoleta.microservicio_plazoleta.infrastructure.configuration;

import com.plazoleta.microservicio_plazoleta.domain.api.IAuthServicePort;
import com.plazoleta.microservicio_plazoleta.domain.api.IDishServicePort;
import com.plazoleta.microservicio_plazoleta.domain.api.IRestaurantServicePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IDishPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IRestaurantPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IUserPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.usecase.DishUseCase;
import com.plazoleta.microservicio_plazoleta.domain.usecase.RestaurantUseCase;
import com.plazoleta.microservicio_plazoleta.infrastructure.configuration.security.JwtAuthServiceAdapter;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.adapter.UserFeignAdapter;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.client.IUserFeignClient;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.mapper.IUserFeignMapper;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.adapter.DishJpaAdapter;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.adapter.RestaurantJpaAdapter;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.mapper.IDishEntityMapper;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.mapper.IRestaurantEntityMapper;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.repository.IDishRepository;
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
    private final IDishRepository dishRepository;
    private final IDishEntityMapper dishEntityMapper;

    @Bean
    public IRestaurantServicePort restaurantService() {
        return new RestaurantUseCase(restaurantPersistencePort(),userPersistencePort());
    }

    @Bean
    public IDishServicePort  dishService() {
        return new DishUseCase(dishPersistencePort(), authServicePort(), userPersistencePort(),restaurantPersistencePort());
    }

    @Bean
    public IRestaurantPersistencePort restaurantPersistencePort(){
        return new RestaurantJpaAdapter(restaurantRepository, restaurantEntityMapper);
    }

    @Bean
    public IUserPersistencePort userPersistencePort() {
        return new UserFeignAdapter(userFeignClient, userFeignMapper);
    }

    @Bean
    public IDishPersistencePort  dishPersistencePort(){
        return new DishJpaAdapter(dishRepository, dishEntityMapper);
    }

    @Bean
    public IAuthServicePort authServicePort() {
        return new JwtAuthServiceAdapter();
    }


}
