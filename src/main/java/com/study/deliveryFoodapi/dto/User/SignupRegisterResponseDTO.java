package com.study.deliveryFoodapi.dto.User;

import java.util.List;

import com.study.deliveryFoodapi.Enums.ERole;
import com.study.deliveryFoodapi.model.User;

import lombok.Data;

@Data
public class SignupRegisterResponseDTO {

    private Long id;
    private String username;
    private String email;
    private List<ERole> roles;

    public SignupRegisterResponseDTO(User u, List<ERole> roles2) {
        this.id = u.getId();
        this.username = u.getUsername();
        this.email = u.getEmail();
        this.roles = roles2;
    }
}
