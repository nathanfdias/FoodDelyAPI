package com.study.deliveryFoodapi.dto.Products;

import com.study.deliveryFoodapi.model.Product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor

public class ProductRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Description is required")
    private String description;
    @NotNull(message = "Price is required")
    private Double price;
    @NotNull(message = "Quantity is required")
    private int quantity; 
    @NotBlank(message = "Image url is required")
    private String imageUrl; 

    @NotNull
    @Valid
    private CategoryToProductRequestDTO category;


    public ProductRequestDTO(Product product){
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.quantity = product.getQuantity();
        this.imageUrl = product.getImageUrl();
        this.category = new CategoryToProductRequestDTO(product.getCategory());
    }
}
