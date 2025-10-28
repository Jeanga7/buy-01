package com.buy_01.ecommerce.product_service.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.buy_01.ecommerce.product_service.model.Seller;

public interface SellerRepository extends MongoRepository<Seller, String> {
    Optional<Seller> findByUserId(String userId);
    boolean existsByUserId(String userId);
}
