package com.study.deliveryFoodapi.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.study.deliveryFoodapi.Enums.EStatus;
import com.study.deliveryFoodapi.dto.Products.ProductIdDTO;
import com.study.deliveryFoodapi.dto.Purchase.PurchaseProductRequestDTO;
import com.study.deliveryFoodapi.dto.Purchase.UserPurchaseRequestDTO;
import com.study.deliveryFoodapi.dto.Purchase.UserPurchaseResponseDTO;
import com.study.deliveryFoodapi.exception.ProductException;
import com.study.deliveryFoodapi.exception.UserException;
import com.study.deliveryFoodapi.exception.UserPurchaseException;
import com.study.deliveryFoodapi.model.Product;
import com.study.deliveryFoodapi.model.Purchase;
import com.study.deliveryFoodapi.model.User;
import com.study.deliveryFoodapi.model.UserPurchase;
import com.study.deliveryFoodapi.repository.ProductRepository;
import com.study.deliveryFoodapi.repository.PurchaseRepository;
import com.study.deliveryFoodapi.repository.UserPurchaseRepository;
import com.study.deliveryFoodapi.repository.UserRepository;
import com.study.deliveryFoodapi.service.UserServices.UserDetailsImplements;

import jakarta.transaction.Transactional;

@Service
public class UserPurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private UserPurchaseRepository userPurchaseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<UserPurchaseResponseDTO> findAllUserPurchases() {
        return userPurchaseRepository.findAll().stream()
                .map(UserPurchaseResponseDTO::new).collect(Collectors.toList());
    }

    public UserPurchaseResponseDTO findUserPurchaseById(Long id) {
        return userPurchaseRepository.findById(id).map(UserPurchaseResponseDTO::new)
                .orElseThrow(() -> new UserPurchaseException("Could not find userPurchase id = " + id));
    }

    @Transactional
    public UserPurchaseResponseDTO insertUserPurchase(UserPurchaseRequestDTO request) {

        UserDetailsImplements userDetails = (UserDetailsImplements) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UserException("Could not find user id = " + userDetails.getId()));

        UserPurchase userPurchase = new UserPurchase();
        userPurchase.setUser(user);
        userPurchase.setPurchaseStatus(EStatus.PENDENTE);
        userPurchase.setPurchaseDate(LocalDateTime.now());
        Double totalPrice = 0.0;
        for (PurchaseProductRequestDTO purchase : request.getPurchases()) {

            Product product = productRepository.findById(purchase.getProduct().getId())
                .orElseThrow(() -> new ProductException("Could not find product, id = " + purchase.getProduct().getId()));

            purchase.setProduct(
                productRepository.findById(purchase.getProduct().getId()).map(ProductIdDTO::new).orElseThrow(
                    () -> new ProductException("Product not found with id " + purchase.getProduct().getId())));
                    totalPrice += product.getPrice() * purchase.getQuantity();
                }
        userPurchase.setTotalPrice(totalPrice);
        userPurchase = userPurchaseRepository.save(userPurchase);


        List<Purchase> purchaseTransfer = new ArrayList<>();
        request.getPurchases().stream().forEach(purchaseProductRequestDTO -> 
            purchaseTransfer.add(purchaseTransferObject(purchaseProductRequestDTO)));

        for (Purchase p : purchaseTransfer){
            p.setUserPurchase(userPurchase);
            purchaseRepository.save(p);
        }

        return new UserPurchaseResponseDTO(userPurchase);
    }

    private Purchase purchaseTransferObject(PurchaseProductRequestDTO purchaseDTO) {
        Purchase purchase = new Purchase();

        Product product = productRepository.findById(purchaseDTO.getProduct().getId())
                .orElseThrow(() -> new ProductException("Could not find product, id = " + purchaseDTO.getProduct().getId()));

        purchase.setProduct(product);
        purchase.setUnitPrice(product.getPrice());
        purchase.setQuantity(purchaseDTO.getQuantity());

        return purchase;
    }
}
