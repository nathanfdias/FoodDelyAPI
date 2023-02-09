package com.study.deliveryFoodapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.study.deliveryFoodapi.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{
    Boolean existsByNameIgnoreCase(String name);
    Page<Product> findByNameContainingIgnoreCaseAndIsActive(String name, Boolean isActive, Pageable pageable);
}
