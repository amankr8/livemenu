package com.flykraft.livemenu.repository;

import com.flykraft.livemenu.entity.AuthUser;
import com.flykraft.livemenu.entity.KitchenOwner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KitchenOwnerRepository extends JpaRepository<KitchenOwner, Long> {
    Optional<KitchenOwner> findByAuthUser(AuthUser authUser);
}
