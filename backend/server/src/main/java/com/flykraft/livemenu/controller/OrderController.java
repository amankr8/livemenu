package com.flykraft.livemenu.controller;

import com.flykraft.livemenu.dto.order.DeliveryLocationDto;
import com.flykraft.livemenu.dto.order.OrderRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/v1/orders")
public interface OrderController {

    @GetMapping
    ResponseEntity<?> getOrdersForKitchen();

    @GetMapping("/user")
    ResponseEntity<?> getOrdersForUser();

    @PostMapping
    ResponseEntity<?> createOrder(@RequestBody OrderRequestDto orderRequestDto);

    @PatchMapping("/{orderId}/update")
    ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestParam String newStatus);

    @DeleteMapping("/{orderId}")
    ResponseEntity<?> cancelOrder(@PathVariable Long orderId);

    @PostMapping("/validate-delivery")
    ResponseEntity<?> validateDelivery(@RequestBody DeliveryLocationDto deliveryLocationDto);
}
