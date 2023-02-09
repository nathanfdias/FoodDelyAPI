package com.study.deliveryFoodapi.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.study.deliveryFoodapi.dto.Products.ProductRequestDTO;
import com.study.deliveryFoodapi.dto.Products.ProductResponseDTO;
import com.study.deliveryFoodapi.exception.CategoryException;
import com.study.deliveryFoodapi.exception.ProductException;
import com.study.deliveryFoodapi.model.Category;
import com.study.deliveryFoodapi.model.Product;
import com.study.deliveryFoodapi.repository.CategoryRepository;
import com.study.deliveryFoodapi.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    
    
    public List<ProductResponseDTO> findAllProducts() {
        return productRepository.findAll().stream()
            .map(ProductResponseDTO::new).collect(Collectors.toList());
    }

    public Page<ProductResponseDTO> searchProducts(String name,Boolean isActive, Pageable pageable) {
        Page<Product> products = productRepository.findByNameContainingIgnoreCaseAndIsActive(name, isActive, pageable);
        return products.map(ProductResponseDTO::new);
    }

    public ProductResponseDTO findProductById(Long id){
        return productRepository.findById(id).map(ProductResponseDTO::new).orElseThrow(() -> new ProductException("Could not find product id = " + id));
    }

    @Transactional
    public ProductResponseDTO insertProduct(ProductRequestDTO product){

        if(productRepository.existsByNameIgnoreCase(product.getName())){
            throw new ProductException("The product already exists");
        }

        Category cat = categoryRepository.findById(product.getCategory().getId()).orElseThrow(() -> new CategoryException("Could not find category id = " + product.getCategory().getId()));
        
        Product prd = new Product();
        prd.setName(product.getName());
        prd.setDescription(product.getDescription());
        prd.setPrice(product.getPrice());
        prd.setQuantity(product.getQuantity());
        prd.setImageUrl(product.getImageUrl());
        prd.setIsActive(true);
        prd.setCategory(cat);
        prd = productRepository.save(prd);

        return new ProductResponseDTO(prd);
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO product){

        Product prd = productRepository.findById(id).orElseThrow(() -> new ProductException("Could not find product id = " + id));

        String name = product.getName();

        if (!prd.getName().equalsIgnoreCase(name) && productRepository.existsByNameIgnoreCase(name)) {
            throw new ProductException("Name already exists for category name = " + name);
        }

        Category cat = categoryRepository.findById(product.getCategory().getId()).orElseThrow(() -> new CategoryException("Could not find category id = " + product.getCategory().getId()));

        prd.setName(product.getName());
        prd.setDescription(product.getDescription());
        prd.setPrice(product.getPrice());
        prd.setQuantity(product.getQuantity());
        prd.setImageUrl(product.getImageUrl());
        prd.setIsActive(true);
        prd.setCategory(cat);

        prd = productRepository.save(prd);
        return new ProductResponseDTO(prd);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductException("Could not find product id = " + id));
    
            product.setIsActive(false);
            productRepository.save(product);
        }
    
}
