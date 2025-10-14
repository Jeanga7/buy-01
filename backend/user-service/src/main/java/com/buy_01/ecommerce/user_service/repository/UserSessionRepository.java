package com.buy_01.ecommerce.user_service.repository;

import com.buy_01.ecommerce.user_service.model.UserSession;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserSessionRepository extends MongoRepository<UserSession, String> {
    Optional<UserSession> findByToken(String token);
    List<UserSession> findByUserId(String userId);
    List<UserSession> findByUserIdAndActiveTrue(String userId);
    boolean existsByUserIdAndDeviceIdAndActiveTrue(String userId, String deviceId);
    List<UserSession> findByUserIdAndDeviceIdAndActiveTrue(String userId, String deviceId);
    void deleteByUserId(String userId);
}
