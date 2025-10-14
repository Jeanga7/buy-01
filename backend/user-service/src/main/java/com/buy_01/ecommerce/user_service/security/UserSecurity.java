package com.buy_01.ecommerce.user_service.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
        
        // Si l'utilisateur est admin, il a accès
        if (hasRole(authentication, "ROLE_ADMIN")) {
            return true;
        }
        
        // Sinon, vérifier si c'est l'utilisateur lui-même
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            return userDetails.getUsername().equals(userId);
        }
        
        return false;
    }
    
    /**
     * Vérifie si l'utilisateur a un rôle spécifique
     * @param authentication L'objet d'authentification Spring Security
     * @param role Le rôle à vérifier (ex: ROLE_ADMIN, ROLE_SELLER, ROLE_CLIENT)
     * @return true si l'utilisateur a le rôle, false sinon
     */
    public boolean hasRole(Authentication authentication, String role) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(role));
    }
    
    /**
     * Vérifie si l'utilisateur est admin
     * @param authentication L'objet d'authentification Spring Security
     * @return true si l'utilisateur est admin, false sinon
     */
    public boolean isAdmin(Authentication authentication) {
        return hasRole(authentication, "ROLE_ADMIN");
    }
    
    /**
     * Vérifie si l'utilisateur peut voir les détails d'un autre utilisateur
     * @param authentication L'objet d'authentification Spring Security
     * @param targetUserId L'ID de l'utilisateur cible
     * @return true si l'utilisateur peut voir les détails, false sinon
     */
    public boolean canViewUser(Authentication authentication, String targetUserId) {
        // Tout utilisateur authentifié peut voir son propre profil
        if (isCurrentUser(authentication, targetUserId)) {
            return true;
        }
        
        // Les admins peuvent voir tous les profils
        return isAdmin(authentication);
    }
    
    /**
     * Vérifie si l'utilisateur peut modifier un autre utilisateur
     * @param authentication L'objet d'authentification Spring Security
     * @param targetUserId L'ID de l'utilisateur cible
     * @return true si l'utilisateur peut modifier, false sinon
     */
    public boolean canEditUser(Authentication authentication, String targetUserId) {
        // Un utilisateur peut modifier son propre profil
        if (isCurrentUser(authentication, targetUserId)) {
            return true;
        }
        
        // Seuls les admins peuvent modifier d'autres utilisateurs
        return isAdmin(authentication);
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
    
    /**
     * Obtient les rôles de l'utilisateur actuellement authentifié
     * @return Une collection de rôles
     */
    public static Collection<String> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
