package com.study.deliveryFoodapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.deliveryFoodapi.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    User findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmailIgnoreCase(String email);
}
