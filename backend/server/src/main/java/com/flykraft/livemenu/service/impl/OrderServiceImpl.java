package com.flykraft.livemenu.service.impl;

import com.flykraft.livemenu.config.TenantContext;
import com.flykraft.livemenu.dto.order.OrderRequestDto;
import com.flykraft.livemenu.entity.*;
import com.flykraft.livemenu.exception.ResourceNotFoundException;
import com.flykraft.livemenu.model.Authority;
import com.flykraft.livemenu.model.OrderStatus;
import com.flykraft.livemenu.repository.CustomerRepository;
import com.flykraft.livemenu.repository.OrderItemRepository;
import com.flykraft.livemenu.repository.OrderRepository;
import com.flykraft.livemenu.service.KitchenService;
import com.flykraft.livemenu.service.MenuService;
import com.flykraft.livemenu.service.OrderService;
import com.flykraft.livemenu.util.AuthUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final KitchenService kitchenService;
    private final MenuService menuService;

    @Override
    public Order loadOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }

    @Transactional
    @Override
    public Order createOrder(OrderRequestDto orderRequestDto) {
        Kitchen kitchen = kitchenService.loadKitchenById(TenantContext.getKitchenId());

        Order order = Order.builder()
                .kitchen(kitchen)
                .status(OrderStatus.PENDING)
                .totalPrice(BigDecimal.ZERO)
                .build();
        order = orderRepository.save(order);

        AuthUser authUser = AuthUtil.getLoggedInUser();
        if (authUser != null && authUser.getAuthority().equals(Authority.CUSTOMER)) {
            Customer customer = customerRepository.findByAuthUser(authUser)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer record is missing for user id: " + authUser.getId()));
            order.setCustomer(customer);
        } else {
            if (orderRequestDto.getGuestName() == null || orderRequestDto.getGuestPhone() == null || orderRequestDto.getGuestAddress() == null) {
                throw new IllegalArgumentException("Guest details are required for placing an order as a guest.");
            }
            order.setGuestName(orderRequestDto.getGuestName());
            order.setGuestPhone(orderRequestDto.getGuestPhone());
            order.setGuestAddress(orderRequestDto.getGuestAddress());
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        for (var itemDto : orderRequestDto.getOrderItems()) {
            MenuItem menuItem = menuService.loadMenuItemById(itemDto.getMenuItemId());
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .menuItem(menuItem)
                    .quantity(itemDto.getQuantity())
                    .price(menuItem.getPrice())
                    .build();
            orderItems.add(orderItem);
            totalPrice = totalPrice.add(menuItem.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
        }
        orderItemRepository.saveAll(orderItems);
        order.setTotalPrice(totalPrice);
        return order;
    }

    @PreAuthorize("hasAuthority('KITCHEN_OWNER')")
    @Override
    public Order updateOrderStatus(Long orderId, String status) {
        Order order = loadOrderById(orderId);
        order.setStatus(OrderStatus.valueOf(status));
        return orderRepository.save(order);
    }

    @PreAuthorize("hasAuthority('KITCHEN_OWNER')")
    @Override
    public void cancelOrder(Long orderId) {
        Order order = loadOrderById(orderId);
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
}
