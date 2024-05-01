package com.order.portal.services.order;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import com.order.portal.models.OAuthAccount;
import com.order.portal.models.order.Order;
import com.order.portal.models.order.OrderStatus;

import com.order.portal.repositories.OrderRepository;

import com.order.portal.services.AuthService;
import com.order.portal.services.NotificationService;
import com.order.portal.services.SequenceGeneratorService;

import com.order.portal.websocket.OrderHandler;
import com.order.portal.websocket.StatisticsHandler;

@Service
@RequiredArgsConstructor
public class OrderUpdateService {
    private final OrderRepository orderRepository;

    private final AuthService authService;
    private final SequenceGeneratorService sequenceGeneratorService;

    private final OrderRetrievalService orderRetrievalService;
    private final OrderStatisticsService orderStatisticsService;

    private final NotificationService notificationService;

    private final OrderHandler orderHandler;
    private final StatisticsHandler statisticsHandler;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public void submitNewOrderForUser(Authentication authentication, Order order) throws AccessDeniedException, IOException {
        OAuthAccount oauthAccount = authService.retrieveAuthenticatedOAuthAccount(authentication);

        order.setId(sequenceGeneratorService.generateSequence(Order.SEQUENCE_NAME));
        order.setCustomerId(oauthAccount.getUserId());
        order.setDate(Instant.now());

        orderRepository.save(order);

        sendUpdates(order);
    }

    public void updateOrderStatus(Long orderId, OrderStatus status) throws IOException {
        boolean orderInChargeExists = orderRepository.existsByStatus(OrderStatus.IN_CHARGE);

        if (status == OrderStatus.IN_CHARGE && orderInChargeExists)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "orderAlreadyInCharge");

        Order order = orderRetrievalService.retrieveOrderById(orderId);
        order.setStatus(status);

        orderRepository.save(order);

        sendUpdates(order);

        OAuthAccount customerAccount = authService.retrieveOAuthAccountByUserId(order.getCustomerId());

        String statusName = status.name().toLowerCase();
        String[] parts = statusName.split("_");

        String messageCode = "orderStatus" +
                parts[0].substring(0, 1).toUpperCase() +
                parts[0].substring(1);

        if (parts.length > 1)
            messageCode += parts[1].substring(0, 1).toUpperCase() +
                    parts[1].substring(1);

        String redirectUrl = "/orders/" + orderId;

        notificationService.saveNotification(customerAccount, messageCode, redirectUrl);
    }

    private void sendUpdates(Order order) throws IOException {
        sendUpdatedOrder(order);
        sendUpdatedStatistics();
    }

    private void sendUpdatedOrder(Order order) throws IOException {
        String orderJson = objectMapper.writeValueAsString(order);

        orderHandler.broadcastMessage(orderJson);
    }

    private void sendUpdatedStatistics() throws IOException {
        Map<String, Long> statistics = orderStatisticsService.retrieveOrderStatistics();

        String statisticsJson = objectMapper.writeValueAsString(statistics);

        statisticsHandler.broadcastMessage(statisticsJson);
    }
}
