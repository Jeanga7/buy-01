package com.buy_01.ecommerce.user_service.dto;

import com.buy_01.ecommerce.user_service.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO pour la requête d'inscription d'un nouvel utilisateur
 */
@Data
public class RegisterRequest {
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String name;
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être une adresse email valide")
    private String email;
    
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;
    
    private String avatar; // URL de l'avatar (optionnel)
    
    @NotNull(message = "Le rôle est obligatoire")
    private User.UserRole role = User.UserRole.CLIENT; // Rôle par défaut: CLIENT
}
