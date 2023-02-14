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

    public ProductResponseDTO findProductById(Long id) {
        return productRepository.findById(id).map(ProductResponseDTO::new)
                .orElseThrow(() -> new ProductException("Could not find product with id " + id));
    }

    @Transactional
    public ProductResponseDTO insertProduct(ProductRequestDTO product) {
        if (productRepository.existsByNameIgnoreCase(product.getName())) {
            throw new ProductException("The category already exists");
        }

        Category cat = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new CategoryException("Could not find category id"
                        + product.getCategory().getId()));

        Product p = new Product();
        p.setName(product.getName());
        p.setDescription(product.getDescription());
        p.setPrice(product.getPrice());
        p.setQuantity(product.getQuantity());
        p.setImageUrl(product.getImageUrl());
        p.setIsActive(true);
        p.setCategory(cat);

        p = productRepository.save(p);

        return new ProductResponseDTO(p);
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductResponseDTO productRequest) {

        Product p = productRepository.findById(id).orElseThrow(() -> new ProductException("Could not find product id"
                + id));

        String name = productRequest.getName();

        if (!p.getName().equalsIgnoreCase(name) && productRepository.existsByNameIgnoreCase(name)) {
            throw new ProductException("Name already exists for category name = " + name);
        }

        Category cat = categoryRepository.findById(productRequest.getCategory().getId())
                .orElseThrow(() -> new CategoryException("Could not find category id"
                        + productRequest.getCategory().getId()));

        p.setName(name);
        p.setDescription(productRequest.getDescription());
        p.setPrice(productRequest.getPrice());
        p.setQuantity(productRequest.getQuantity());
        p.setImageUrl(productRequest.getImageUrl());
        p.setIsActive(true);
        p.setCategory(cat);

        p = productRepository.save(p);
        return new ProductResponseDTO(p);
    }

    @Transactional

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Could not find product id=" + id));

        product.setIsActive(false);
        productRepository.save(product);
    }

    public Page<ProductResponseDTO> findAllProductsPageable(Boolean isActive, Pageable pageable) {
        Page<Product> products = productRepository.findByIsActive(isActive, pageable);
        return products.map(ProductResponseDTO::new);
    }

    public Page<ProductResponseDTO> findAllProductsByCategyPageable(String categoryName, Boolean isActive,
            Pageable pageable) {
        Page<Product> products = productRepository.findByCategory_NameIgnoreCaseAndIsActive(categoryName, isActive,
                pageable);
        return products.map(ProductResponseDTO::new);
    }

    public Page<ProductResponseDTO> searchProductsByCategories(String name, String categoryName, boolean isActive,
            Pageable pageable) {
        if (pageable == null || pageable.getPageNumber() < 0 || pageable.getPageSize() < 1) {
            throw new ProductException("Invalid page request");
        }

        Page<Product> products = productRepository.findByNameContainingIgnoreCaseAndCategory_NameIgnoreCaseAndIsActive(
                name,
                categoryName, isActive, pageable);

        if (products == null || products.isEmpty()) {
            throw new ProductException(
                    "No products found for name: " + name + " and isActive: " + isActive + " and category: "
                            + categoryName);
        }

        return products.map(ProductResponseDTO::new);
    }

    public Page<ProductResponseDTO> searchProducts(String name, Boolean isActive, Pageable pageable) {

        if (pageable == null || pageable.getPageNumber() < 0 || pageable.getPageSize() < 1) {
            throw new ProductException("Invalid page request");
        }

        Page<Product> products = productRepository.findByNameContainingIgnoreCaseAndIsActive(name, isActive, pageable);

        if (products == null || products.isEmpty()) {
            throw new ProductException("No products found for name: " + name + " and isActive: " + isActive);
        }

        return products.map(ProductResponseDTO::new);
    }

}