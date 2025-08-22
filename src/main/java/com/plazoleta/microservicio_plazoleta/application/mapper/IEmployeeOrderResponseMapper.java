package com.plazoleta.microservicio_plazoleta.application.mapper;

import com.plazoleta.microservicio_plazoleta.application.dto.response.OrderItemResponseDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.OrderResponseDto;
import com.plazoleta.microservicio_plazoleta.application.dto.response.PageResponse;
import com.plazoleta.microservicio_plazoleta.domain.model.Dish;
import com.plazoleta.microservicio_plazoleta.domain.model.Order;
import com.plazoleta.microservicio_plazoleta.domain.model.OrderItem;
import com.plazoleta.microservicio_plazoleta.domain.util.PageResult;
import org.mapstruct.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IEmployeeOrderResponseMapper {

    @Named("toPage")
    default PageResponse<OrderResponseDto> toPage(PageResult<Order> page,
                                                  @Context Map<Long, Dish> dishesById) {
        PageResponse<OrderResponseDto> resp = new PageResponse<>();
        resp.setItems(page.getItems().stream()
                .map(o -> toResponse(o, dishesById))
                .toList());
        resp.setPage(page.getPage());
        resp.setSize(page.getSize());
        resp.setTotalElements(page.getTotalElements());
        resp.setTotalPages(page.getTotalPages());
        return resp;
    }

    @Mapping(target = "items", expression = "java(toItemDetails(order.getItems(), dishesById))")
    OrderResponseDto toResponse(Order order, @Context Map<Long, Dish> dishesById);

    @Named("toItemDetails")
    default List<OrderItemResponseDto> toItemDetails(List<OrderItem> items,
                                                     @Context Map<Long, Dish> dishesById) {
        if (items == null || items.isEmpty()) return Collections.emptyList();

        return items.stream()
                .map(oi -> {
                    Dish d = dishesById.get(oi.getDishId());
                    OrderItemResponseDto dto = new OrderItemResponseDto();
                    dto.setDishId(oi.getDishId());
                    dto.setQuantity(oi.getQuantity());
                    if (d != null) {
                        dto.setName(d.getName());
                        dto.setDescription(d.getDescription());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
