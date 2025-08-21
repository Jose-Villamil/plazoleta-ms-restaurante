package com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.adapter;

import com.plazoleta.microservicio_plazoleta.domain.model.Restaurant;
import com.plazoleta.microservicio_plazoleta.domain.spi.IRestaurantPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.util.PageResult;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.mapper.IRestaurantEntityMapper;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Optional;

@RequiredArgsConstructor
public class RestaurantJpaAdapter implements IRestaurantPersistencePort {

    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;

    @Override
    public void saveRestaurant(Restaurant restaurant) {
        restaurantRepository.save(restaurantEntityMapper.toEntity(restaurant));
    }

    @Override
    public Optional<Restaurant> findRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .map(restaurantEntityMapper::toRestaurant);
    }

    @Override
    public PageResult<Restaurant> findAllOrderByNameAsc(int page, int size) {
        var pageable = PageRequest.of(page, size,
                Sort.by("name").ascending());
        var result = restaurantRepository.findAll(pageable);

        var items = result.getContent().stream()
                .map(restaurantEntityMapper::toRestaurant)
                .toList();

        return new PageResult<>(
                items,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }
}
