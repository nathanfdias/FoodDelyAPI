package com.study.deliveryFoodapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.study.deliveryFoodapi.exception.CategoryException;
import com.study.deliveryFoodapi.model.Product;
import com.study.deliveryFoodapi.repository.ProductRepository;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public Product findProductById(Long id){
        return productRepository.findById(id).orElseThrow(() -> new CategoryException("Could not find category id = " + id));
    }
}
