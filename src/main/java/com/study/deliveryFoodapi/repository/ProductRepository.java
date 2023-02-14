package com.study.deliveryFoodapi.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.study.deliveryFoodapi.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Boolean existsByNameIgnoreCase(String name);

    Page<Product> findByNameContainingIgnoreCaseAndIsActive(String name, Boolean isActive, Pageable pageable);

    Optional<Product> findProductByNameIgnoreCaseAndIsActive(String name, boolean isActive);

    Page<Product> findByNameContainingIgnoreCaseAndCategory_NameIgnoreCaseAndIsActive(String name, String categoryName,
            boolean isActive, Pageable pageable);

    Page<Product> findByCategory_NameIgnoreCaseAndIsActive(String categoryName, boolean isActive, Pageable pageable);

    Page<Product> findByIsActive(Boolean isActive, Pageable pageable);

    Optional<Product> findProductByNameIgnoreCase(String name);

}
