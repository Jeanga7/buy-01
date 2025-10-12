package com.buy_01.ecommerce.user_service.controller;

import com.buy_01.ecommerce.user_service.dto.RegisterRequest;
import com.buy_01.ecommerce.user_service.dto.UserResponse;
import com.buy_01.ecommerce.user_service.mapper.UserMapper;
import com.buy_01.ecommerce.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Enregistre un nouvel utilisateur
     * @param registerRequest Les informations d'inscription de l'utilisateur
     * @return L'utilisateur créé avec le statut HTTP 201 (Created)
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        var user = userMapper.toUser(registerRequest);
        var createdUser = userService.registerUser(user);
        var userResponse = userMapper.toUserResponse(createdUser);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }
}
