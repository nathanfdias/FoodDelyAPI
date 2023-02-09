package com.study.deliveryFoodapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.deliveryFoodapi.dto.User.LoginRequestDTO;
import com.study.deliveryFoodapi.dto.User.RefreshTokenRequestDTO;
import com.study.deliveryFoodapi.dto.User.RefreshTokenResponseDTO;
import com.study.deliveryFoodapi.dto.User.RoleRequestDTO;
import com.study.deliveryFoodapi.dto.User.SignupRegisterResponseDTO;
import com.study.deliveryFoodapi.dto.User.SignupRequestDTO;
import com.study.deliveryFoodapi.dto.User.SignupResponseDTO;
import com.study.deliveryFoodapi.exception.AccountException;
import com.study.deliveryFoodapi.exception.ApiError;
import com.study.deliveryFoodapi.exception.RefreshTokenException;
import com.study.deliveryFoodapi.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "auth", description = "Autentificação de usuario")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signin")
    @Operation(summary = "Sign In Service", description = "Sign In Service", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully Singned In!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SignupResponseDTO.class))),
            @ApiResponse(responseCode = "400", ref = "BadRequest"),
            @ApiResponse(responseCode = "401", ref = "badcredentials"),
            @ApiResponse(responseCode = "422", ref = "unprocessableEntity"),
            @ApiResponse(responseCode = "500", ref = "internalServerError")
    })
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            SignupResponseDTO signupResponse = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok().header("Authorization", signupResponse.getAccessToken()).body(signupResponse);
        } catch (AccountException e) {
            return ResponseEntity.unprocessableEntity().body(
                    new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", e.getLocalizedMessage()));
        }
    }

    @PostMapping("/signup")
    @Operation(summary = "Register In Service", description = "register In Service", responses = {
            @ApiResponse(responseCode = "201", description = "Successfully Register In!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SignupRegisterResponseDTO.class))),
            @ApiResponse(responseCode = "400", ref = "BadRequest"),
            @ApiResponse(responseCode = "401", ref = "badcredentials"),
            @ApiResponse(responseCode = "422", ref = "unprocessableEntity"),
            @ApiResponse(responseCode = "500", ref = "internalServerError")
    })
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignupRequestDTO signUpRequest) {
        try {
            SignupRegisterResponseDTO response = authService.registerUser(signUpRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.unprocessableEntity().body(
                    new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", e.getLocalizedMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/new-roles/{idUsuario}")
    @SecurityRequirement(name = "token")
    @Operation(summary = "Add news Roles", description = "add news Roles, Admin Only", responses = {
            @ApiResponse(responseCode = "201", description = "Roles Register In!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SignupRegisterResponseDTO.class))),
            @ApiResponse(responseCode = "400", ref = "BadRequest"),
            @ApiResponse(responseCode = "401", ref = "badcredentials"),
            @ApiResponse(responseCode = "403", ref = "forbidden"),
            @ApiResponse(responseCode = "422", ref = "unprocessableEntity"),
            @ApiResponse(responseCode = "500", ref = "internalServerError")
    })
    public ResponseEntity<Object> newRoles(@Valid @RequestBody RoleRequestDTO rolesIn, @PathVariable Long idUsuario) {
        try {
            SignupRegisterResponseDTO response = authService.newRoles(rolesIn, idUsuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (AccountException e) {
            return ResponseEntity.unprocessableEntity().body(
                    new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", e.getLocalizedMessage()));
        }
    }

    @PostMapping("/refreshtoken")
    @Operation(summary = "Refresh Token", description = "Refresh Token", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully Refresh Token!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RefreshTokenRequestDTO.class))),
            @ApiResponse(responseCode = "400", ref = "BadRequest"),
            @ApiResponse(responseCode = "401", ref = "badcredentials"),
            @ApiResponse(responseCode = "422", ref = "unprocessableEntity"),
            @ApiResponse(responseCode = "500", ref = "internalServerError")
    })
    public ResponseEntity<Object> refreshtoken(@Valid @RequestBody RefreshTokenRequestDTO request) {
        try {
            RefreshTokenResponseDTO response = authService.refreshtoken(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RefreshTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/signout")
    @SecurityRequirement(name = "token")
    @Operation(summary = "Signout In Service", description = "Signout In Service", responses = {
            @ApiResponse(responseCode = "200", description = "Log out successful!", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", ref = "BadRequest"),
            @ApiResponse(responseCode = "401", ref = "badcredentials"),
            @ApiResponse(responseCode = "422", ref = "unprocessableEntity"),
            @ApiResponse(responseCode = "500", ref = "internalServerError")
    })
    public ResponseEntity<String> logoutUser() {
        return ResponseEntity.ok(authService.logoutUser());
    }

}
