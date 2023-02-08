package com.study.deliveryFoodapi.dto.Products;

import com.study.deliveryFoodapi.model.Category;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryToProductRequestDTO {
    
    @NotNull
    private Long id;

    public CategoryToProductRequestDTO(Category category){
        this.id = category.getId();
    }
}
