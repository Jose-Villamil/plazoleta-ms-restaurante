package com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.adapter;

import com.plazoleta.microservicio_plazoleta.domain.model.Dish;
import com.plazoleta.microservicio_plazoleta.domain.spi.IDishPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.util.PageResult;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.entity.DishEntity;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.mapper.IDishEntityMapper;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.repository.IDishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

@RequiredArgsConstructor
public class DishJpaAdapter implements IDishPersistencePort {

    private final IDishRepository dishRepository;
    private final IDishEntityMapper dishEntityMapper;

    @Override
    public void saveDish(Dish dish) {
        dishRepository.save(dishEntityMapper.toEntity(dish));
    }

    @Override
    public void updateDish(Dish dish) {
        dishRepository.save(dishEntityMapper.toEntity(dish));
    }

    @Override
    public Optional<Dish> findDishById(Long dishId) {
        return dishRepository.findById(dishId)
                .map(dishEntityMapper::toDish);
    }

    @Override
    public PageResult<Dish> findActiveByRestaurantOrderByNameAsc(Long restaurantId, Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<DishEntity> result;

        if (categoryId != null) {
            result = dishRepository.findByRestaurantIdAndCategoryIdAndActiveTrue(restaurantId, categoryId, pageable);
        } else {
            result = dishRepository.findByRestaurantIdAndActiveTrue(restaurantId, pageable);
        }

        var items = result.getContent().stream().map(dishEntityMapper::toDish).toList();

        return new PageResult<>(items, result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }
}
