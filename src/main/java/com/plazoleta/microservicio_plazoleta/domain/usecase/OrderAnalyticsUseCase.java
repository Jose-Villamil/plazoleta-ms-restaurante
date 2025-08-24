package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.api.IAuthServicePort;
import com.plazoleta.microservicio_plazoleta.domain.api.IOrderAnalyticsServicePort;
import com.plazoleta.microservicio_plazoleta.domain.model.Order;
import com.plazoleta.microservicio_plazoleta.domain.model.OrderStatus;
import com.plazoleta.microservicio_plazoleta.domain.model.Tracelog;
import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.spi.IOrderPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.IRestaurantPersistencePort;
import com.plazoleta.microservicio_plazoleta.domain.spi.ITraceLogOutPort;
import com.plazoleta.microservicio_plazoleta.domain.util.PageResult;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static com.plazoleta.microservicio_plazoleta.domain.util.DomainMessages.NOT_RESTAURANT_OWNER;
import static com.plazoleta.microservicio_plazoleta.domain.util.DomainMessages.RESTAURANT_NOT_FOUND;

public class OrderAnalyticsUseCase implements IOrderAnalyticsServicePort {

    private final IAuthServicePort auth;
    private final IRestaurantPersistencePort restaurantPort;
    private final IOrderPersistencePort orderPort;
    private final ITraceLogOutPort tracePort;

    public OrderAnalyticsUseCase(IAuthServicePort auth, IRestaurantPersistencePort restaurantPort,
                                 IOrderPersistencePort orderPort, ITraceLogOutPort tracePort) {
        this.auth = auth;
        this.restaurantPort = restaurantPort;
        this.orderPort = orderPort;
        this.tracePort = tracePort;
    }

    @Override
    public List<OrderDuration> listOrderDurations(Long restaurantId, int page, int size) {
        ensureOwner(restaurantId);
        PageResult<Order> pageResult =
                orderPort.findByRestaurantAndStatus(restaurantId, OrderStatus.ENTREGADO, page, size);

        List<Order> orders = pageResult.getItems();
        List<OrderDuration> out = new ArrayList<>(orders.size());
        for (Order order : orders) {
            var traces = tracePort.findClientTrace(order.getId(), order.getClientId());
            Optional<Instant> started = traces.stream()
                    .filter(t -> "EN_PREPARACION".equalsIgnoreCase(t.getNewStatus()))
                    .map(Tracelog::getAt)
                    .min(Comparator.naturalOrder());

            Instant createdAt = order.getCreatedAt().toInstant(ZoneOffset.UTC);
            Instant start = started.orElse(createdAt);

            Optional<Instant> finished = traces.stream()
                    .filter(t -> "ENTREGADO".equalsIgnoreCase(t.getNewStatus()))
                    .map(Tracelog::getAt)
                    .max(Comparator.naturalOrder());
            if (finished.isEmpty()) continue;

            long seconds = Math.max(0, finished.get().getEpochSecond() - start.getEpochSecond());
            out.add(new OrderDuration(order.getId(), order.getChefId(), start, finished.get(), seconds));
        }
        return out;
    }

    @Override
    public List<EmployeeEfficiency> employeeRanking(Long restaurantId, int page, int size, int minOrders) {
        List<OrderDuration> durations = listOrderDurations(restaurantId, page, size);
        Map<Long, List<Long>> byEmployee = durations.stream()
                .filter(d -> d.chefId() != null)
                .collect(Collectors.groupingBy(OrderDuration::chefId,
                        Collectors.mapping(OrderDuration::durationSeconds, Collectors.toList())));

        List<EmployeeEfficiency> out = new ArrayList<>();
        for (var e : byEmployee.entrySet()) {
            List<Long> secs = e.getValue();
            if (secs.size() < Math.max(1, minOrders)) continue;
            Collections.sort(secs);
            long avg = Math.round(secs.stream().mapToLong(Long::longValue).average().orElse(0));
            long median = percentile(secs, 50);
            long p90 = percentile(secs, 90);
            out.add(new EmployeeEfficiency(e.getKey(), avg, median, p90, secs.size()));
        }

        out.sort(Comparator.comparingLong(EmployeeEfficiency::avgSeconds));
        return out;
    }

    private static long percentile(List<Long> sortedAsc, int p) {
        if (sortedAsc.isEmpty()) return 0;
        if (p <= 0) return sortedAsc.get(0);
        if (p >= 100) return sortedAsc.get(sortedAsc.size()-1);
        double rank = (p/100.0) * (sortedAsc.size()-1);
        int low = (int)Math.floor(rank);
        int high = (int)Math.ceil(rank);
        if (low == high) return sortedAsc.get(low);
        double w = rank - low;
        return Math.round(sortedAsc.get(low) + w * (sortedAsc.get(high) - sortedAsc.get(low)));
    }

    private void ensureOwner(Long restaurantId) {
        var restaurant = restaurantPort.findRestaurantById(restaurantId)
                .orElseThrow(() -> new DomainException(RESTAURANT_NOT_FOUND));
        Long me = auth.getAuthenticatedUserId();
        if (!restaurant.getIdOwner().equals(me)) {
            throw new DomainException(NOT_RESTAURANT_OWNER);
        }
    }
}

