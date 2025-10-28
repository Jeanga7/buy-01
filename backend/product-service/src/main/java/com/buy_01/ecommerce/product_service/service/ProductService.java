package com.buy_01.ecommerce.product_service.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.buy_01.ecommerce.product_service.dto.ProductRequest;
import com.buy_01.ecommerce.product_service.dto.ProductResponse;
import com.buy_01.ecommerce.product_service.model.Product;
import com.buy_01.ecommerce.product_service.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest, String userId) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity())
                .userId(userId)
                .build();
        
        Product savedProduct = productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }

    public Optional<ProductResponse> getProductById(String id) {
        return productRepository.findById(id)
                .map(this::mapToProductResponse);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getProductsByUserId(String userId) {
        return productRepository.findByUserId(userId).stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<ProductResponse> updateProduct(String id, ProductRequest productRequest, String userId) {
        return productRepository.findByIdAndUserId(id, userId)
                .map(existingProduct -> {
                    existingProduct.setName(productRequest.getName());
                    existingProduct.setDescription(productRequest.getDescription());
                    existingProduct.setPrice(productRequest.getPrice());
                    existingProduct.setQuantity(productRequest.getQuantity());
                    Product updatedProduct = productRepository.save(existingProduct);
                    return mapToProductResponse(updatedProduct);
                });
    }
    
    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .userId(product.getUserId())
                .build();
    }

    @Transactional
    public boolean deleteProduct(String id, String userId) {
        return productRepository.findByIdAndUserId(id, userId)
                .map(product -> {
                    productRepository.delete(product);
                    return true;
                })
                .orElse(false);
    }
}
