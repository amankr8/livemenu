package com.flykraft.menu_service.repository;

import com.flykraft.menu_service.model.Kitchen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KitchenRepository extends JpaRepository<Kitchen, Long> {
    Optional<Kitchen> findBySubdomain(String kitchenSubdomain);
}
