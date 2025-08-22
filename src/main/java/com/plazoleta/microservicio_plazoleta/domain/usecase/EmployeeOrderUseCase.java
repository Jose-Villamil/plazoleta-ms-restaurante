package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.api.IAuthServicePort;
import com.plazoleta.microservicio_plazoleta.domain.api.IEmployeeOrderServicePort;
import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.model.Order;
import com.plazoleta.microservicio_plazoleta.domain.model.OrderStatus;
import com.plazoleta.microservicio_plazoleta.domain.spi.IOrderPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IRestaurantEmployeePersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.util.PageResult;

import static com.plazoleta.microservicio_plazoleta.domain.util.DomainMessages.FIELD_REQUIRED;

public class EmployeeOrderUseCase implements IEmployeeOrderServicePort {

    private final IAuthServicePort authServicePort;
    private final IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort;
    private final IOrderPersistencePort orderPersistencePort;

    public EmployeeOrderUseCase(IAuthServicePort authServicePort,
                                     IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort,
                                     IOrderPersistencePort orderPersistencePort) {
        this.authServicePort = authServicePort;
        this.restaurantEmployeePersistencePort = restaurantEmployeePersistencePort;
        this.orderPersistencePort = orderPersistencePort;
    }

    @Override
    public PageResult<Order> listOrdersByStatus(OrderStatus status, int page, int size) {
        if (status == null) throw new DomainException(String.format(FIELD_REQUIRED, "Estado"));
        if (page < 0) page = 0;
        if (size <= 0) size = 10;

        Long employeeId = authServicePort.getAuthenticatedUserId();

        Long restaurantId = restaurantEmployeePersistencePort.findRestaurantIdByEmployeeId(employeeId)
                .orElseThrow(() -> new DomainException("El empleado no tiene restaurante asociado"));

        return orderPersistencePort.findByRestaurantAndStatus(restaurantId, status, page, size);
    }
}
