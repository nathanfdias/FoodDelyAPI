package com.study.deliveryFoodapi.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.study.deliveryFoodapi.dto.Category.CategoryRequestDTO;
import com.study.deliveryFoodapi.dto.Category.CategoryResponseDTO;
import com.study.deliveryFoodapi.dto.Category.CategoryResponseListDTO;
import com.study.deliveryFoodapi.exception.ApiError;
import com.study.deliveryFoodapi.exception.CategoryException;
import com.study.deliveryFoodapi.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponseListDTO>> findAll(){
        return ResponseEntity.ok(categoryService.findAllCategories());
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id){

        try {
            return ResponseEntity.ok(categoryService.findCategoryById(id));
        } catch (CategoryException ex) {
            return ResponseEntity.unprocessableEntity()
                .body(new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", ex.getLocalizedMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<Object> createCategory(@Valid @RequestBody CategoryRequestDTO categoryRequest){
        
        try{
            CategoryResponseDTO response = categoryService.insertCategory(categoryRequest);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

            return ResponseEntity.created(uri).body(response);
        } catch(CategoryException ex){
            return ResponseEntity.unprocessableEntity()
            .body(new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", ex.getLocalizedMessage()));
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequestDTO categoryRequest){
        
        try{
            CategoryResponseDTO response = categoryService.updateCategory(id, categoryRequest);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
            return ResponseEntity.created(uri).body(response);
        } catch (CategoryException ex) {
            return ResponseEntity.unprocessableEntity()
            .body(new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", ex.getLocalizedMessage()));
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deleteCategory(@PathVariable Long id){

        try{
            categoryService.deleteCategory(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (CategoryException | DataIntegrityViolationException e) {
            return ResponseEntity.unprocessableEntity()
                .body(new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", e.getLocalizedMessage()));
        }
    }
}
