package com.flykraft.livemenu.repository;

import com.flykraft.livemenu.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
