package com.plazoleta.microservicio_plazoleta.infrastructure.configuration;

import com.plazoleta.microservicio_plazoleta.domain.api.*;
import com.plazoleta.microservicio_plazoleta.domain.spi.*;
import com.plazoleta.microservicio_plazoleta.domain.usecase.*;
import com.plazoleta.microservicio_plazoleta.infrastructure.configuration.security.JwtAuthServiceAdapter;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.adapter.NotificationFeignAdapter;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.adapter.UserFeignAdapter;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.client.INotificationFeignClient;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.client.IUserFeignClient;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.mapper.IUserFeignMapper;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.adapter.DishJpaAdapter;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.adapter.OrderJpaAdapter;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.adapter.RestaurantEmployeeJpaAdapter;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.adapter.RestaurantJpaAdapter;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.mapper.IDishEntityMapper;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.mapper.IOrderEntityMapper;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.mapper.IRestaurantEntityEmployeeMapper;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.mapper.IRestaurantEntityMapper;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.repository.IDishRepository;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.repository.IOrderRepository;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.repository.IRestaurantEmployeeRepository;
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

    private final IRestaurantEmployeeRepository  restaurantEmployeeRepository;
    private final IRestaurantEntityEmployeeMapper restaurantEmployeeMapper;

    private final IOrderRepository orderRepository;
    private final IOrderEntityMapper orderEntityMapper;

    private final INotificationFeignClient  notificationFeignClient;

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

    @Bean
    public IRestaurantEmployeeServicePort restaurantEmployeeServicePort(){
        return new RestaurantEmployeeUseCase(restaurantEmployeePersistencePort(), restaurantPersistencePort(),authServicePort());
    }
    @Bean
    public IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort(){
        return new RestaurantEmployeeJpaAdapter(restaurantEmployeeRepository, restaurantEmployeeMapper);
    }

    @Bean
    public IOrderServicePort orderServicePort() {
        return new OrderUseCase(orderPersistencePort(), authServicePort(), restaurantPersistencePort(), dishPersistencePort());
    }

    @Bean
    public IOrderPersistencePort orderPersistencePort() {
        return new OrderJpaAdapter(orderRepository, orderEntityMapper);
    }

    @Bean
    public IEmployeeOrderServicePort employeeOrderQueryService() {
        return new EmployeeOrderUseCase(authServicePort(), restaurantEmployeePersistencePort(), orderPersistencePort(), userPersistencePort(),restaurantPersistencePort(),notificationOutPort(notificationFeignClient));
    }

    @Bean
    public INotificationOutPort notificationOutPort(INotificationFeignClient client) {
        return new NotificationFeignAdapter(client);
    }

}
