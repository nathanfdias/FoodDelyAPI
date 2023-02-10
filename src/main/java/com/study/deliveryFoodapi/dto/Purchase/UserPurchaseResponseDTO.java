package com.study.deliveryFoodapi.dto.Purchase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.study.deliveryFoodapi.Enums.EStatus;
import com.study.deliveryFoodapi.dto.User.UserResponseDTO;
import com.study.deliveryFoodapi.model.UserPurchase;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserPurchaseResponseDTO {

    private Long id;
    private UserResponseDTO user;
    private double totalPrice;
    private EStatus purchaseStatus;
    private LocalDateTime purchaseDate;
    private List<PurchaseResponseDTO> purchases = new ArrayList<>();

    public UserPurchaseResponseDTO(UserPurchase userPurchase) {
        this.id = userPurchase.getId();
        this.user = new UserResponseDTO(userPurchase.getUser());
        this.totalPrice = userPurchase.getTotalPrice();
        this.purchaseStatus = userPurchase.getPurchaseStatus();
        this.purchaseDate = userPurchase.getPurchaseDate();
        if (userPurchase.getPurchases() != null) {
            userPurchase.getPurchases().forEach(purchase -> purchases.add(new PurchaseResponseDTO(purchase)));
        }
    }

}
