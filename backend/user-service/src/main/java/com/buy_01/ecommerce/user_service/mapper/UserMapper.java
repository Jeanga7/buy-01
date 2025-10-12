package com.buy_01.ecommerce.user_service.mapper;

import org.springframework.stereotype.Component;

import com.buy_01.ecommerce.user_service.dto.RegisterRequest;
import com.buy_01.ecommerce.user_service.dto.UserResponse;
import com.buy_01.ecommerce.user_service.model.User;

import lombok.RequiredArgsConstructor;

/**
 * Mapper pour la conversion entre les entités User et les DTOs
 */
@Component
@RequiredArgsConstructor
public class UserMapper {
    
    /**
     * Convertit un RegisterRequest en User
     */
    public User toUser(RegisterRequest registerRequest) {
        if (registerRequest == null) {
            return null;
        }
        
        return User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword()) // Le mot de passe sera encodé dans le service
                .build();
    }
    
    /**
     * Convertit un User en UserResponse
     */
    public UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }
        
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .avatar(user.getAvatar())
                .build();
    }
}
