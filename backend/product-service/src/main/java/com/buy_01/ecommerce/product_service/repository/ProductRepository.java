package com.buy_01.ecommerce.product_service.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.buy_01.ecommerce.product_service.model.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByUserId(String userId);
}
