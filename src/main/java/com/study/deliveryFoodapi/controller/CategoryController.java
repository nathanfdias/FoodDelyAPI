package com.study.deliveryFoodapi.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Search Category", description = "Search Category", responses = {
            @ApiResponse(responseCode = "200", description = "Category found!"),
            @ApiResponse(responseCode = "400", ref = "BadRequest"),
            @ApiResponse(responseCode = "401", ref = "badcredentials"),
            @ApiResponse(responseCode = "422", ref = "unprocessableEntity"),
            @ApiResponse(responseCode = "500", ref = "internalServerError")
    })
    public ResponseEntity<List<CategoryResponseListDTO>> findAll() {
        return ResponseEntity.ok(categoryService.findAllCategories());
    }

    @GetMapping("{id}")
    @Operation(summary = "Search Category", description = "Search Category", responses = {
            @ApiResponse(responseCode = "200", description = "Category found!"),
            @ApiResponse(responseCode = "400", ref = "BadRequest"),
            @ApiResponse(responseCode = "401", ref = "badcredentials"),
            @ApiResponse(responseCode = "422", ref = "unprocessableEntity"),
            @ApiResponse(responseCode = "500", ref = "internalServerError")
    })
    public ResponseEntity<Object> findById(@PathVariable Long id) {

        try {
            return ResponseEntity.ok(categoryService.findCategoryById(id));
        } catch (CategoryException ex) {
            return ResponseEntity.unprocessableEntity()
                    .body(new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity",
                            ex.getLocalizedMessage()));
        }
    }

    @PostMapping()
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create Category", description = "Create Category", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully category created!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponseDTO.class))),
            @ApiResponse(responseCode = "400", ref = "BadRequest"),
            @ApiResponse(responseCode = "401", ref = "badcredentials"),
            @ApiResponse(responseCode = "422", ref = "unprocessableEntity"),
            @ApiResponse(responseCode = "500", ref = "internalServerError")
    })
    public ResponseEntity<Object> createCategory(@Valid @RequestBody CategoryRequestDTO categoryRequest) {

        try {
            CategoryResponseDTO response = categoryService.insertCategory(categoryRequest);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(response.getId())
                    .toUri();

            return ResponseEntity.created(uri).body(response);
        } catch (CategoryException ex) {
            return ResponseEntity.unprocessableEntity()
                    .body(new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity",
                            ex.getLocalizedMessage()));
        }
    }

    @PutMapping("{id}")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Category", description = "Update Category", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully category updated!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponseDTO.class))),
            @ApiResponse(responseCode = "400", ref = "BadRequest"),
            @ApiResponse(responseCode = "401", ref = "badcredentials"),
            @ApiResponse(responseCode = "422", ref = "unprocessableEntity"),
            @ApiResponse(responseCode = "500", ref = "internalServerError")
    })
    public ResponseEntity<Object> updateCategory(@PathVariable Long id,
            @Valid @RequestBody CategoryRequestDTO categoryRequest) {

        try {
            CategoryResponseDTO response = categoryService.updateCategory(id, categoryRequest);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(response.getId())
                    .toUri();
            return ResponseEntity.created(uri).body(response);
        } catch (CategoryException ex) {
            return ResponseEntity.unprocessableEntity()
                    .body(new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity",
                            ex.getLocalizedMessage()));
        }
    }

    @DeleteMapping("{id}")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete Category", description = "Delete Category", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully category deleted!"),
            @ApiResponse(responseCode = "400", ref = "BadRequest"),
            @ApiResponse(responseCode = "401", ref = "badcredentials"),
            @ApiResponse(responseCode = "422", ref = "unprocessableEntity"),
            @ApiResponse(responseCode = "500", ref = "internalServerError")
    })
    public ResponseEntity<Object> deleteCategory(@PathVariable Long id) {

        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (CategoryException | DataIntegrityViolationException e) {
            return ResponseEntity.unprocessableEntity()
                    .body(new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity",
                            e.getLocalizedMessage()));
        }
    }
}
