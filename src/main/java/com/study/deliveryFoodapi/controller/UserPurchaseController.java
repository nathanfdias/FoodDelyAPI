package com.study.deliveryFoodapi.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.study.deliveryFoodapi.dto.Purchase.UserPurchaseRequestDTO;
import com.study.deliveryFoodapi.dto.Purchase.UserPurchaseResponseDTO;
import com.study.deliveryFoodapi.exception.ApiError;
import com.study.deliveryFoodapi.exception.UserPurchaseException;
import com.study.deliveryFoodapi.service.UserPurchaseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/userPurchase")
public class UserPurchaseController {
    @Autowired
    private UserPurchaseService userPurchaseService;

    @GetMapping
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Search All UserPurchase", description = "Search All UserPurchase", responses = {
            @ApiResponse(responseCode = "200", description = "UserPurchase found!"),
            @ApiResponse(responseCode = "400", ref = "BadRequest"),
            @ApiResponse(responseCode = "401", ref = "badcredentials"),
            @ApiResponse(responseCode = "422", ref = "unprocessableEntity"),
            @ApiResponse(responseCode = "500", ref = "internalServerError")
    })
    public ResponseEntity<List<UserPurchaseResponseDTO>> findAll() {
        return ResponseEntity.ok(userPurchaseService.findAllUserPurchases());
    }

    @GetMapping("{id}")
    @Operation(summary = "Search All UserPurchase", description = "Search All UserPurchase", responses = {
            @ApiResponse(responseCode = "200", description = "UserPurchase found!"),
            @ApiResponse(responseCode = "400", ref = "BadRequest"),
            @ApiResponse(responseCode = "401", ref = "badcredentials"),
            @ApiResponse(responseCode = "422", ref = "unprocessableEntity"),
            @ApiResponse(responseCode = "500", ref = "internalServerError")
    })
    public ResponseEntity<Object> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userPurchaseService.findUserPurchaseById(id));
        } catch (UserPurchaseException ex) {
            return ResponseEntity.unprocessableEntity()
                    .body(new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity",
                            ex.getLocalizedMessage()));
        }
    }

    @PostMapping()
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Create UserPurchase", description = "Create UserPurchase", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully userPurchase created!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserPurchaseResponseDTO.class))),
            @ApiResponse(responseCode = "400", ref = "BadRequest"),
            @ApiResponse(responseCode = "401", ref = "badcredentials"),
            @ApiResponse(responseCode = "422", ref = "unprocessableEntity"),
            @ApiResponse(responseCode = "500", ref = "internalServerError")
    })
    public ResponseEntity<Object> createUserPurchase(@Valid @RequestBody UserPurchaseRequestDTO userPurchaseRequest) {

        try {
            UserPurchaseResponseDTO response = userPurchaseService.insertUserPurchase(userPurchaseRequest);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(response.getId())
                    .toUri();

            return ResponseEntity.created(uri).body(response);
        } catch (UserPurchaseException ex) {
            return ResponseEntity.unprocessableEntity()
                    .body(new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity",
                            ex.getLocalizedMessage()));
        }
    }
}
