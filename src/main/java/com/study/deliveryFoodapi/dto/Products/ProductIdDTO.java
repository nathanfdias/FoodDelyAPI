package com.study.deliveryFoodapi.dto.Products;

import com.study.deliveryFoodapi.model.Product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductIdDTO {

    private Long id;

    public ProductIdDTO(Product product) {
        this.id = product.getId();
    }
}
