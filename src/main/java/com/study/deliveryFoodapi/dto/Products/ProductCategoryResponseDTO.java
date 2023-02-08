package com.study.deliveryFoodapi.dto.Products;

import com.study.deliveryFoodapi.model.Product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ProductCategoryResponseDTO {
    
    private Long id;
    private String name;
    private String description;
    private Double price;
    private int quantity;
    private Boolean isActive;
    private String imageUrl;

    public ProductCategoryResponseDTO(Product product){
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.quantity = product.getQuantity();
        this.isActive = product.getIsActive();
        this.imageUrl = product.getImageUrl();
    }
}
