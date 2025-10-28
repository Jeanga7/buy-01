package com.buy_01.ecommerce.product_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.buy_01.ecommerce.product_service.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByUserId(String userId);
    Optional<Product> findByIdAndUserId(String id, String userId);
}
