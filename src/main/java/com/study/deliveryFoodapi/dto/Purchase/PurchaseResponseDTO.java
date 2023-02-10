package com.study.deliveryFoodapi.dto.Purchase;

import com.study.deliveryFoodapi.dto.Products.ProductResponseDTO;
import com.study.deliveryFoodapi.model.Purchase;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PurchaseResponseDTO {

    private int quantity;
    private double unitPrice;
    private ProductResponseDTO product;

    public PurchaseResponseDTO(Purchase purchase) {
        this.quantity = purchase.getQuantity();
        this.unitPrice = purchase.getUnitPrice();
        this.product = new ProductResponseDTO(purchase.getProduct());
    }
}
