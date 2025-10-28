package com.buy_01.ecommerce.user_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service de publication d'événements liés aux utilisateurs vers Kafka
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventPublisher {

    private static final String USER_TOPIC = "user-events";

    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Publie un événement de création de vendeur
     * @param userId L'ID de l'utilisateur devenu vendeur
     */
    public void publishSellerCreatedEvent(String userId) {
        String message = "SELLER_CREATED:" + userId;
        log.info("Publishing SELLER_CREATED event for user: {}", userId);
        kafkaTemplate.send(USER_TOPIC, message)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully published SELLER_CREATED event for user: {}", userId);
                } else {
                    log.error("Failed to publish SELLER_CREATED event for user: {}", userId, ex);
                }
            });
    }
}