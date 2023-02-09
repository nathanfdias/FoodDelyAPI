package com.study.deliveryFoodapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.deliveryFoodapi.Enums.ERole;
import com.study.deliveryFoodapi.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
