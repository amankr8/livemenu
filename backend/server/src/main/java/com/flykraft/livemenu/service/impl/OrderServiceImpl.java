package com.flykraft.livemenu.service.impl;

import com.flykraft.livemenu.config.TenantContext;
import com.flykraft.livemenu.dto.order.DeliveryLocationDto;
import com.flykraft.livemenu.dto.order.OrderRequestDto;
import com.flykraft.livemenu.dto.user.AddressReqDto;
import com.flykraft.livemenu.dto.user.UserReqDto;
import com.flykraft.livemenu.entity.*;
import com.flykraft.livemenu.exception.ResourceNotFoundException;
import com.flykraft.livemenu.model.OrderStatus;
import com.flykraft.livemenu.repository.OrderItemRepository;
import com.flykraft.livemenu.repository.OrderRepository;
import com.flykraft.livemenu.service.KitchenService;
import com.flykraft.livemenu.service.MenuService;
import com.flykraft.livemenu.service.OrderService;
import com.flykraft.livemenu.service.UserService;
import com.flykraft.livemenu.util.AuthUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final KitchenService kitchenService;
    private final MenuService menuService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public Order loadOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }

    @Override
    public List<Order> loadAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> loadAllUserOrders() {
        User currentUser = userService.loadCurrentUser();
        return orderRepository.findByUser(currentUser);
    }

    @Transactional
    @Override
    public Order createOrder(OrderRequestDto orderRequestDto) {
        Kitchen kitchen = kitchenService.loadKitchenById(TenantContext.getKitchenId());
        AuthUser authUser = AuthUtil.getLoggedInUser();

        UserReqDto userReqDto = orderRequestDto.getCustomerDetails();
        if (userReqDto.getPhone() == null || userReqDto.getPhone().isEmpty()) {
            userReqDto.setPhone(authUser.getUsername());
        }

        User user = userService.loadCurrentUser();
        AddressReqDto addressReqDto = orderRequestDto.getAddressDetails();
        Order order = Order.builder()
                .kitchen(kitchen)
                .user(user)
                .customerName(userReqDto.getName())
                .customerPhone(userReqDto.getPhone())
                .deliveryStreet(addressReqDto.getStreetAddress())
                .deliveryAddress(addressReqDto.getFullAddress())
                .deliveryLocation(addressReqDto.getLocation())
                .status(OrderStatus.PENDING)
                .subtotal(BigDecimal.ZERO)
                .packingCharges(BigDecimal.valueOf(15))
                .deliveryFees(BigDecimal.valueOf(40))
                .taxes(BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO)
                .specialInstructions(orderRequestDto.getNotes())
                .build();
        order = orderRepository.save(order);

        BigDecimal subTotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        for (var itemDto : orderRequestDto.getOrderItems()) {
            MenuItem menuItem = menuService.loadMenuItemById(itemDto.getMenuItemId());
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .itemName(menuItem.getName())
                    .quantity(itemDto.getQuantity())
                    .price(menuItem.getPrice())
                    .build();
            orderItems.add(orderItem);
            subTotal = subTotal.add(menuItem.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
        }
        orderItemRepository.saveAll(orderItems);
        order.setOrderItems(orderItems);

        order.setSubtotal(subTotal);
        BigDecimal totalAmount = order.getSubtotal()
                .add(order.getPackingCharges())
                .add(order.getDeliveryFees());
        order.setTotalAmount(totalAmount);

        String topic = "/topic/kitchen/" + kitchen.getId();
        messagingTemplate.convertAndSend(topic, order.toResponseDto());

        return order;
    }

    @Override
    public Order updateOrderStatus(Long orderId, String newStatus) {
        Order order = loadOrderById(orderId);
        order.setStatus(OrderStatus.valueOf(newStatus));
        Order updatedOrder = orderRepository.save(order);

        String userTopic = "/topic/user/" + updatedOrder.getUser().getId();
        messagingTemplate.convertAndSend(userTopic, updatedOrder.toResponseDto());

        return updatedOrder;
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = loadOrderById(orderId);
        order.setStatus(OrderStatus.CANCELLED);
        Order updatedOrder = orderRepository.save(order);

        String userTopic = "/topic/user/" + updatedOrder.getUser().getId();
        messagingTemplate.convertAndSend(userTopic, updatedOrder.toResponseDto());
    }

    @Override
    public Boolean validateDelivery(DeliveryLocationDto deliveryLocationDto) {
        Kitchen kitchen = kitchenService.loadKitchenById(TenantContext.getKitchenId());

        if (kitchen.getDeliveryRadius() == null || kitchen.getLocation() == null) {
            return true;
        }

        String[] locationParts = kitchen.getLocation().split(",");
        if (locationParts.length != 2) {
            return true;
        }

        Double kitchenLat = Double.parseDouble(locationParts[0].trim());
        Double kitchenLng = Double.parseDouble(locationParts[1].trim());

        double distance = calculateDistance(kitchenLat, kitchenLng, deliveryLocationDto.getLat(), deliveryLocationDto.getLng());
        return distance <= kitchen.getDeliveryRadius();
    }

    /**
     * Calculate distance between two coordinates using Haversine formula
     * Returns distance in kilometers
     */
    private double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int EARTH_RADIUS = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
}
