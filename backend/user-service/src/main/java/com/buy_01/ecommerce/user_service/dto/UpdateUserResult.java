package com.buy_01.ecommerce.user_service.dto;

import com.buy_01.ecommerce.user_service.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Classe pour représenter le résultat d'une mise à jour d'utilisateur
 * Contient l'utilisateur mis à jour et éventuellement un nouveau token JWT
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserResult {
    private User user;
    private String newToken;
}
