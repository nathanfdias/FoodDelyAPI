package com.study.deliveryFoodapi.dto.User;

import java.util.Set;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RoleRequestDTO {

    @NotEmpty
    private Set<String> roles;

}

