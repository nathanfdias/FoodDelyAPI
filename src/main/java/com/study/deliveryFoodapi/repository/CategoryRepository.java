package com.study.deliveryFoodapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.deliveryFoodapi.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{
    Boolean existsByNameIgnoreCase(String name);
}
