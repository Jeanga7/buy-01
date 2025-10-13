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
        
        // Utilise l'avatar fourni ou un avatar par défaut
        String avatar = registerRequest.getAvatar();
        if (avatar == null || avatar.trim().isEmpty()) {
            avatar = "https://www.gravatar.com/avatar/00000000000000000000000000000000?d=mp&f=y";
        }
        
        return User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword()) // Le mot de passe sera encodé dans le service
                .avatar(avatar)
                .role(registerRequest.getRole()) // Ajout du rôle
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
    
    /**
     * Alias pour toUserResponse
     */
    public UserResponse toDto(User user) {
        return toUserResponse(user);
    }
}
