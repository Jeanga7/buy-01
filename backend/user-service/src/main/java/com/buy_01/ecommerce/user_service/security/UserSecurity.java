package com.buy_01.ecommerce.user_service.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Classe utilitaire pour la sécurité personnalisée
 */
@Component("userSecurity")
public class UserSecurity {
    
    /**
     * Vérifie si l'utilisateur actuellement authentifié est l'utilisateur demandé
     * @param authentication L'objet d'authentification Spring Security
     * @param userId L'ID de l'utilisateur à vérifier
     * @return true si l'utilisateur est le même que celui authentifié, false sinon
     */
    public boolean isCurrentUser(Authentication authentication, String userId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            // Ici, nous supposons que le nom d'utilisateur est l'email
            // Vous devrez peut-être ajuster cela en fonction de votre implémentation
            return userDetails.getUsername().equals(userId);
        }
        
        return false;
    }
    
    /**
     * Obtient l'identifiant de l'utilisateur actuellement authentifié
     * @return L'identifiant de l'utilisateur ou null si non authentifié
     */
    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            }
        }
        return null;
    }
}
