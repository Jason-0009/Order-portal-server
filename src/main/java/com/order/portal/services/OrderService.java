package com.order.portal.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import com.order.portal.models.OAuthAccount;
import com.order.portal.models.order.Order;

import com.order.portal.repositories.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final AuthService authService;

    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public void placeOrder(Order order) {
        OAuthAccount oauthAccount = authService.getAuthenticatedOAuthAccount();
        
        String userId = oauthAccount.getUserId();

        order.setUserId(userId);

        orderRepository.save(order);
    }
}
