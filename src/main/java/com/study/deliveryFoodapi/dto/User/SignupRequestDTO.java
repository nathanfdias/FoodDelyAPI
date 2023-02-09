package com.study.deliveryFoodapi.dto.User;

import com.study.deliveryFoodapi.model.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequestDTO {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    public SignupRequestDTO(User u) {
        this.username = u.getUsername();
        this.email = u.getEmail();
        this.password = u.getPassword();
    }
}
