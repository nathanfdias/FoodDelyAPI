package com.study.deliveryFoodapi.dto.Purchase;

import java.time.LocalDateTime;
import java.util.List;

import com.study.deliveryFoodapi.Enums.EStatus;
import com.study.deliveryFoodapi.model.UserPurchase;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserPurchaseRequestDTO {

    private double totalPrice;
    private EStatus purchaseStatus;
    private LocalDateTime purchaseDate;
    private List<PurchaseProductRequestDTO> purchases;

    public UserPurchaseRequestDTO(UserPurchase userPurchase) {
        this.totalPrice = userPurchase.getTotalPrice();
        this.purchaseStatus = userPurchase.getPurchaseStatus();
        this.purchaseDate = userPurchase.getPurchaseDate();
        if (userPurchase.getPurchases() != null) {
            userPurchase.getPurchases().forEach(purchase -> purchases.add(new PurchaseProductRequestDTO(purchase)));
        }
    }
}
