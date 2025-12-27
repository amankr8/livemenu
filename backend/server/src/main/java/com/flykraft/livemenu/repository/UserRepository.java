package com.flykraft.livemenu.repository;

import com.flykraft.livemenu.entity.AuthUser;
import com.flykraft.livemenu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByAuthUser(AuthUser authUser);
    Optional<User> findByPhone(String phone);
}
