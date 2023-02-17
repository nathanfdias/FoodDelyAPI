package com.study.deliveryFoodapi.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.study.deliveryFoodapi.Enums.ERole;
import com.study.deliveryFoodapi.dto.User.LoginRequestDTO;
import com.study.deliveryFoodapi.dto.User.RefreshTokenRequestDTO;
import com.study.deliveryFoodapi.dto.User.RefreshTokenResponseDTO;
import com.study.deliveryFoodapi.dto.User.RoleRequestDTO;
import com.study.deliveryFoodapi.dto.User.SignupRegisterResponseDTO;
import com.study.deliveryFoodapi.dto.User.SignupRequestDTO;
import com.study.deliveryFoodapi.dto.User.SignupResponseDTO;
import com.study.deliveryFoodapi.dto.User.UserLoggedResponseDTO;
import com.study.deliveryFoodapi.exception.AccountException;
import com.study.deliveryFoodapi.exception.RefreshTokenException;
import com.study.deliveryFoodapi.exception.UserException;
import com.study.deliveryFoodapi.model.RefreshToken;
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
            throw new AccountException("Error: Username já em uso!");
        }

        if (userRepository.existsByEmailIgnoreCase(signUpRequest.getEmail())) {
            throw new AccountException("Error: Email já em uso!");
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

    @Transactional
    public RefreshTokenResponseDTO refreshtoken(RefreshTokenRequestDTO request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    if (!user.getIsActive()) {
                        refreshTokenService.deleteByUserId(user.getId());
                        throw new RefreshTokenException(requestRefreshToken, "Refresh token is not in database!");
                    }
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername(), user.getId());
                    List<Role> roles = user.getRoles().stream().collect(Collectors.toList());
                    List<ERole> rolesList = roles.stream().map(Role::getName).collect(Collectors.toList());
                    return new RefreshTokenResponseDTO(token, requestRefreshToken, user.getId(),
                            user.getUsername(), user.getEmail(), rolesList);
                })
                .orElseThrow(() -> new RefreshTokenException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

    @Transactional
    public SignupRegisterResponseDTO removeRoles(RoleRequestDTO rolesIn, Long idUsuario) {
        Optional<User> user = userRepository.findById(idUsuario);

        if (!user.isPresent()) {
            throw new AccountException("Error: User notFound");
        }

        Set<String> strRoles = rolesIn.getRoles();
        Set<Role> roles = new HashSet<>();

        for (String role : strRoles) {
            ERole eRole;
            switch (role) {
                case "admin":
                    eRole = ERole.ROLE_ADMIN;
                    break;
                default:
                    eRole = null;
            }
            Role foundRole = roleRepository.findByName(eRole)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(foundRole);
        }

        Set<Role> currentRoles = user.get().getRoles();
        for (Role role : roles) {
            if (role.getName().equals(ERole.ROLE_USER) && !currentRoles.contains(role)) {
                throw new AccountException("Error: ROLE_USER cannot be removed");
            }
        }

        currentRoles.removeAll(roles);
        user.get().setRoles(currentRoles);
        userRepository.save(user.get());

        List<ERole> rolesList = currentRoles.stream().map(Role::getName).collect(Collectors.toList());

        return new SignupRegisterResponseDTO(user.get(), rolesList);
    }

    @Transactional
    public SignupRegisterResponseDTO newRoles(RoleRequestDTO rolesIn, Long idUsuario) {
        Optional<User> user = userRepository.findById(idUsuario);

        if (!user.isPresent()) {
            throw new AccountException("Error: User notFound");
        }

        Set<String> strRoles = rolesIn.getRoles();
        Set<Role> roles = new HashSet<>();

        for (String role : strRoles) {
            ERole eRole;
            switch (role) {
                case "admin":
                    eRole = ERole.ROLE_ADMIN;
                    break;
                default:
                    eRole = ERole.ROLE_USER;
            }
            Role foundRole = roleRepository.findByName(eRole)
                    .orElseThrow(() -> new AccountException("Error: Role is not found."));
            roles.add(foundRole);
        }

        Set<Role> currentRoles = user.get().getRoles();
        currentRoles.addAll(roles);
        user.get().setRoles(currentRoles);
        userRepository.save(user.get());

        List<ERole> rolesList = currentRoles.stream().map(Role::getName).collect(Collectors.toList());

        return new SignupRegisterResponseDTO(user.get(), rolesList);
    }

    @Transactional
    public SignupResponseDTO authenticateUser(LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImplements userDetails = (UserDetailsImplements) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return new SignupResponseDTO(jwt, refreshToken.getToken(), userDetails.getId(), userDetails.getUsername(),
                userDetails.getEmail(), roles);
    }

    public UserLoggedResponseDTO findLoggedUser() {
        UserDetailsImplements userDetails = (UserDetailsImplements) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return userRepository.findById(userDetails.getId()).map(UserLoggedResponseDTO::new)
                .orElseThrow(() -> new UserException("Could not find user"));
    }
}
