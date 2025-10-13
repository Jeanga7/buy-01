package com.buy_01.ecommerce.user_service.dto;

import java.time.LocalDateTime;

/**
 * Réponse d'authentification contenant le token JWT et les informations de session
 */
public record AuthenticationResponse(
    String token,
    String message,
    String deviceId,
    LocalDateTime expiresAt
) {
    // Constructeur avec token et message
    public AuthenticationResponse(String token, String message) {
        this(token, message, null, null);
    }
    
    // Constructeur avec token uniquement
    public AuthenticationResponse(String token) {
        this(token, null, null, null);
    }
}

