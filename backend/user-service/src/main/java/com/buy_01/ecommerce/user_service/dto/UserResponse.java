package com.buy_01.ecommerce.user_service.dto;

import com.buy_01.ecommerce.user_service.model.User.UserRole;

import lombok.Builder;
import lombok.Data;

/**
 * DTO pour la réponse d'un utilisateur
 */
@Data
@Builder
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private UserRole role;
    private String avatar;
}
