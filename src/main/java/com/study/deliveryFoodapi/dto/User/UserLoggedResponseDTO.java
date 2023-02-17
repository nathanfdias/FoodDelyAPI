package com.study.deliveryFoodapi.dto.User;

import java.util.HashSet;
import java.util.Set;

import com.study.deliveryFoodapi.model.Role;
import com.study.deliveryFoodapi.model.User;

import lombok.Data;

@Data
public class UserLoggedResponseDTO {

    private Long id;
    private String username;
    private String email;
    private Boolean isActive;
    private Set<Role> roles = new HashSet<>();

    public UserLoggedResponseDTO(User u) {
        this.id = u.getId();
        this.username = u.getUsername();
        this.email = u.getEmail();
        this.isActive = u.getIsActive();
        this.roles = u.getRoles();
    }
}
