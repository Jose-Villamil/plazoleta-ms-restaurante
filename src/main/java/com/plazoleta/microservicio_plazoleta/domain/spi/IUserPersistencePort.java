package com.plazoleta.microservicio_plazoleta.domain.spi;

import com.plazoleta.microservicio_plazoleta.domain.model.User;

import java.util.Optional;

public interface IUserPersistencePort {
    Optional<User> findById(Long id);
}
