package com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.adapter;

import com.plazoleta.microservicio_plazoleta.domain.model.User;
import com.plazoleta.microservicio_plazoleta.domain.spi.IUserPersistencePort;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.client.IUserFeignClient;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.mapper.IUserFeignMapper;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class UserFeignAdapter implements IUserPersistencePort {

    private final IUserFeignClient userFeignClient;
    private final IUserFeignMapper userFeignMapper;

    @Override
    public Optional<User> findById(Long id) {
        try {
            return Optional.ofNullable(
                    userFeignMapper.toUser(userFeignClient.getUserById(id))
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
