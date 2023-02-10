package com.study.deliveryFoodapi.dto.Purchase;

import com.study.deliveryFoodapi.dto.Products.ProductIdDTO;
import com.study.deliveryFoodapi.model.Purchase;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PurchaseProductRequestDTO {

    private int quantity;
    private double unitPrice;
    private ProductIdDTO product;

    public PurchaseProductRequestDTO(Purchase purchase) {
        this.quantity = purchase.getQuantity();
        this.unitPrice = purchase.getUnitPrice();
        this.product = new ProductIdDTO(purchase.getProduct());

    }
}
