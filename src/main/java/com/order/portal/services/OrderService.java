package com.order.portal.services;

import java.time.Instant;

import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;

import org.springframework.security.core.Authentication;

import com.order.portal.models.OAuthAccount;
import com.order.portal.models.order.Order;

import com.order.portal.repositories.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final AuthService authService;

    public Page<Order> getOrdersByUser(Authentication authentication, Pageable pageable) throws AccessDeniedException {
        OAuthAccount oauthAccount = authService.getAuthenticatedOAuthAccount(authentication);

        String userId = oauthAccount.getUserId();

        return orderRepository.findByUserId(userId, pageable);
    }

    public Order getOrderById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found."));
    }

    public void placeOrder(Authentication authentication, Order order) throws AccessDeniedException {
        OAuthAccount oauthAccount = authService.getAuthenticatedOAuthAccount(authentication);

        String userId = oauthAccount.getUserId();
        order.setUserId(userId);

        Instant orderDate = Instant.now();
        order.setDate(orderDate);

        orderRepository.save(order);
    }
}
