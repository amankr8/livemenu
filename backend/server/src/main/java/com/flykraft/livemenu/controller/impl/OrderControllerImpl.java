package com.flykraft.livemenu.controller.impl;

import com.flykraft.livemenu.controller.OrderController;
import com.flykraft.livemenu.dto.order.DeliveryLocationDto;
import com.flykraft.livemenu.dto.order.OrderRequestDto;
import com.flykraft.livemenu.entity.Order;
import com.flykraft.livemenu.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class OrderControllerImpl implements OrderController {
    private final OrderService orderService;

    @Override
    public ResponseEntity<?> getOrdersForKitchen() {
        return ResponseEntity.ok(orderService.loadAllOrders().stream().map(Order::toResponseDto).toList());
    }

    @Override
    public ResponseEntity<?> getOrdersForUser() {
        return ResponseEntity.ok(orderService.loadAllUserOrders().stream().map(Order::toResponseDto).toList());
    }

    @Override
    public ResponseEntity<?> createOrder(OrderRequestDto orderRequestDto) {
        Order order = orderService.createOrder(orderRequestDto);
        return ResponseEntity.ok(order.toResponseDto());
    }

    @Override
    public ResponseEntity<?> updateOrderStatus(Long orderId, String newStatus) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, newStatus).toResponseDto());
    }

    @Override
    public ResponseEntity<?> cancelOrder(Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<?> validateDelivery(DeliveryLocationDto deliveryLocationDto) {
        Boolean isDeliveryValid = orderService.validateDelivery(deliveryLocationDto);
        return ResponseEntity.ok(isDeliveryValid);
    }
}
