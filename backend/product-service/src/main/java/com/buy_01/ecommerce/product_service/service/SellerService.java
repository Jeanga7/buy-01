package com.buy_01.ecommerce.product_service.service;

import com.buy_01.ecommerce.product_service.model.Seller;
import com.buy_01.ecommerce.product_service.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;

    /**
     * Crée un nouveau vendeur s'il n'existe pas déjà
     * @param userId L'ID de l'utilisateur à enregistrer comme vendeur
     * @return Le vendeur créé ou existant
     */
    public Seller createSellerIfNotExists(String userId) {
        return sellerRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Seller newSeller = Seller.builder()
                            .userId(userId)
                            .active(true)
                            .build();
                    return sellerRepository.save(newSeller);
                });
    }

    /**
     * Vérifie si un utilisateur est un vendeur actif
     * @param userId L'ID de l'utilisateur à vérifier
     * @return true si l'utilisateur est un vendeur actif, false sinon
     */
    public boolean isActiveSeller(String userId) {
        return sellerRepository.findByUserId(userId)
                .map(Seller::isActive)
                .orElse(false);
    }
}
