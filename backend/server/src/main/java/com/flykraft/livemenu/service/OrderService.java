package com.flykraft.livemenu.service;

import com.flykraft.livemenu.dto.order.OrderRequestDto;
import com.flykraft.livemenu.entity.Order;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface OrderService {

    @PreAuthorize("hasAuthority('KITCHEN_OWNER')")
    List<Order> loadOrdersByKitchen(Long kitchenId);

    Order loadOrderById(Long orderId);

    @PreAuthorize("hasAuthority('USER')")
    Order createOrder(OrderRequestDto orderRequestDto);

    @PreAuthorize("hasAuthority('KITCHEN_OWNER')")
    Order updateOrderStatus(Long orderId, String status);

    @PreAuthorize("hasAuthority('KITCHEN_OWNER')")
    void cancelOrder(Long orderId);
}
