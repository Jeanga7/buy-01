package com.buy_01.ecommerce.user_service.service;

import com.buy_01.ecommerce.user_service.model.UserSession;
import com.buy_01.ecommerce.user_service.repository.UserSessionRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSessionService {
    private final UserSessionRepository sessionRepository;
    
    public UserSession createSession(String userId, String token, String deviceId, 
                                   String ipAddress, String userAgent, long expirationInMs) {
        UserSession session = new UserSession();
        session.setUserId(userId);
        session.setToken(token);
        session.setDeviceId(deviceId);
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        session.setCreatedAt(LocalDateTime.now());
        session.setExpiresAt(LocalDateTime.now().plusSeconds(expirationInMs / 1000));
        return sessionRepository.save(session);
    }
    
    public boolean isSessionValid(String userId, String deviceId) {
        return sessionRepository.existsByUserIdAndDeviceIdAndActiveTrue(userId, deviceId);
    }
    
    @Transactional
    public void invalidateSession(String token) {
        sessionRepository.findByToken(token).ifPresent(session -> {
            session.setActive(false);
            sessionRepository.save(session);
        });
    }
    
    @Transactional
    public void invalidateAllSessions(String userId) {
        sessionRepository.findByUserId(userId).forEach(session -> {
            session.setActive(false);
            sessionRepository.save(session);
        });
    }
    
    public List<UserSession> getActiveSessions(String userId) {
        return sessionRepository.findByUserIdAndActiveTrue(userId);
    }
    
    /**
     * Invalide toutes les sessions actives pour un appareil spécifique
     * @param userId L'ID de l'utilisateur
     * @param deviceId L'ID de l'appareil
     */
    @Transactional
    public void invalidateSessionsForDevice(String userId, String deviceId) {
        sessionRepository.findByUserIdAndDeviceIdAndActiveTrue(userId, deviceId)
            .forEach(session -> {
                session.setActive(false);
                sessionRepository.save(session);
            });
    }
    
    @Transactional
    public void revokeSession(String userId, String sessionId) {
        sessionRepository.findById(sessionId)
            .filter(session -> session.getUserId().equals(userId))
            .ifPresent(session -> {
                session.setActive(false);
                sessionRepository.save(session);
            });
    }
    
    public String generateDeviceId(HttpServletRequest request, String userAgent) {
        String ip = request.getRemoteAddr();
        String acceptLanguage = request.getHeader("Accept-Language");
        String accept = request.getHeader("Accept");
        
        String deviceInfo = String.format("%s|%s|%s|%s", 
            userAgent, 
            ip, 
            acceptLanguage != null ? acceptLanguage : "", 
            accept != null ? accept : ""
        );
        
        return UUID.nameUUIDFromBytes(deviceInfo.getBytes()).toString();
    }
}
