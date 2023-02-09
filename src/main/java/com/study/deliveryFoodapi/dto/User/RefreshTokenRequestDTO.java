package com.study.deliveryFoodapi.dto.User;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class RefreshTokenRequestDTO {

    @NotBlank
    private String refreshToken;
}
