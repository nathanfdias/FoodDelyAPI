package com.study.deliveryFoodapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.deliveryFoodapi.model.UserPurchase;

public interface UserPurchaseRepository extends JpaRepository<UserPurchase, Long> {

}
