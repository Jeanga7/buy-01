package com.buy_01.ecommerce.product_service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.buy_01.ecommerce.product_service.model.Seller;
import com.buy_01.ecommerce.product_service.service.SellerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Listener Kafka pour les événements liés aux utilisateurs
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final SellerService sellerService;

    /**
     * Écoute les événements utilisateur et traite les événements de création de vendeur
     * @param message Le message reçu de Kafka
     */
    @KafkaListener(topics = "user-events", groupId = "product-group")
    public void listenUserEvents(String message) {
        log.info("Received user event: {}", message);
        
        if (message.startsWith("SELLER_CREATED:")) {
            String userId = message.substring("SELLER_CREATED:".length()).trim();
            if (!userId.isEmpty()) {
                handleSellerCreated(userId);
            } else {
                log.warn("Received empty user ID in SELLER_CREATED event");
            }
        }
    }

    /**
     * Traite l'événement de création d'un nouveau vendeur
     * @param userId L'ID de l'utilisateur devenu vendeur
     */
    private void handleSellerCreated(String userId) {
        try {
            log.info("Traitement de la création d'un nouveau vendeur avec l'ID: {}", userId);
            Seller seller = sellerService.createSellerIfNotExists(userId);
            log.info("Profil vendeur créé ou mis à jour avec succès pour l'utilisateur: {}", userId);
        } catch (Exception e) {
            log.error("Erreur lors du traitement de la création du vendeur: {}", userId, e);
        }
    }
}
