package com.study.deliveryFoodapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.deliveryFoodapi.model.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

}
