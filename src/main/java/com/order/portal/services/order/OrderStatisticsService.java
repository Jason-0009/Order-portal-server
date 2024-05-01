package com.order.portal.services.order;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


import lombok.RequiredArgsConstructor;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.order.portal.repositories.OrderRepository;
import com.order.portal.models.order.OrderStatus;

import static com.order.portal.utils.TimeUtils.calculateStartAndEndOfDay;

@Service
@RequiredArgsConstructor
public class OrderStatisticsService {
    private final OrderRepository orderRepository;

    public Map<String, Long> retrieveOrderStatistics() {
        Map<String, Long> statistics = new HashMap<>();

        statistics.put("deliveredToday", countOrdersDeliveredToday());
        statistics.put("pending", countPendingOrders());
        statistics.put("delivering", countDeliveringOrders());

        return statistics;
    }

    private long countOrdersDeliveredToday() {
        Instant now = Instant.now();

        Pair<Instant, Instant> startAndEndOfDay = calculateStartAndEndOfDay(now);

        Instant startOfDay = startAndEndOfDay.getFirst();
        Instant endOfDay = startAndEndOfDay.getSecond();

        return orderRepository.countByDateBetweenAndStatus(startOfDay, endOfDay, OrderStatus.DELIVERED);
    }

    private long countPendingOrders() {
        return orderRepository.countByStatus(OrderStatus.PENDING);
    }

    private long countDeliveringOrders() {
        return orderRepository.countByStatus(OrderStatus.DELIVERING);
    }
}
