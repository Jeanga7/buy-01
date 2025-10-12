package com.buy_01.ecommerce.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    
    // Champ pour la confirmation du mot de passe
    @NotBlank(message = "La confirmation du mot de passe est obligatoire")
    private String confirmPassword;
}
