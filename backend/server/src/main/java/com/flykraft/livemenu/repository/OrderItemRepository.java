package com.flykraft.livemenu.repository;

import com.flykraft.livemenu.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
