package com.buy_01.ecommerce.user_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service pour gérer la liste noire des tokens JWT invalides
 */
@Service
public class TokenBlacklistService {
    
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    
    /**
     * Ajoute un token à la liste noire
     * @param token Le token à invalider
     */
    public void invalidateToken(String token) {
        if (token != null && !token.isEmpty()) {
            blacklistedTokens.add(token);
        }
    }
    
    /**
     * Vérifie si un token est dans la liste noire
     * @param token Le token à vérifier
     * @return true si le token est invalide, false sinon
     */
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
    
    /**
     * Nettoie périodiquement les tokens expirés de la liste noire
     */
    @Scheduled(fixedRate = 3600000) // Toutes les heures
    public void cleanupExpiredTokens() {
        Date now = new Date();
        Set<String> tokensToRemove = new HashSet<>();
        
        for (String token : blacklistedTokens) {
            try {
                Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
                
                if (claims.getExpiration().before(now)) {
                    tokensToRemove.add(token);
                }
            } catch (Exception e) {
                // Si le token est invalide, on le supprime de la liste noire
                tokensToRemove.add(token);
            }
        }
        
        blacklistedTokens.removeAll(tokensToRemove);
    }
}
