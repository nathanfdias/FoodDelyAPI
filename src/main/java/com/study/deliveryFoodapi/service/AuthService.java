package com.study.deliveryFoodapi.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.study.deliveryFoodapi.Enums.ERole;
import com.study.deliveryFoodapi.dto.User.SignupRegisterResponseDTO;
import com.study.deliveryFoodapi.dto.User.SignupRequestDTO;
import com.study.deliveryFoodapi.exception.AccountException;
import com.study.deliveryFoodapi.model.Role;
import com.study.deliveryFoodapi.model.User;
import com.study.deliveryFoodapi.repository.RoleRepository;
import com.study.deliveryFoodapi.repository.UserRepository;
import com.study.deliveryFoodapi.service.UserServices.RefreshTokenService;
import com.study.deliveryFoodapi.service.UserServices.UserDetailsImplements;
import com.study.deliveryFoodapi.utils.JwtUtils;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Transactional
    public SignupRegisterResponseDTO registerUser(SignupRequestDTO signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new AccountException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmailIgnoreCase(signUpRequest.getEmail())) {
            throw new AccountException("Error: Email is already in use!");
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setIsActive(true);

        Set<Role> roles = new HashSet<>();

        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);

        user.setRoles(roles);
        userRepository.save(user);

        List<ERole> rolesList = roles.stream().map(Role::getName).collect(Collectors.toList());

        return new SignupRegisterResponseDTO(user, rolesList);
    }

    @Transactional
    public String logoutUser() {
        UserDetailsImplements userDetails = (UserDetailsImplements) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Long userId = userDetails.getId();
        refreshTokenService.deleteByUserId(userId);
        return "Log out successful!";
    }

}
