package com.study.deliveryFoodapi.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.study.deliveryFoodapi.dto.Purchase.PurchaseResponseDTO;
import com.study.deliveryFoodapi.exception.ProductException;
import com.study.deliveryFoodapi.repository.PurchaseRepository;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    public List<PurchaseResponseDTO> findAllPurchases() {
        return purchaseRepository.findAll().stream()
                .map(PurchaseResponseDTO::new).collect(Collectors.toList());
    }

    public PurchaseResponseDTO findPurchaseById(Long id) {
        return purchaseRepository.findById(id).map(PurchaseResponseDTO::new)
                .orElseThrow(() -> new ProductException("Could not find purchase id = " + id));
    }
}
