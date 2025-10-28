package com.buy_01.ecommerce.product_service.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buy_01.ecommerce.product_service.dto.ProductRequest;
import com.buy_01.ecommerce.product_service.dto.ProductResponse;
import com.buy_01.ecommerce.product_service.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> createProduct(
            @Valid @RequestBody ProductRequest productRequest, 
            @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentification requise");
        }
        try {
            ProductResponse createdProduct = productService.createProduct(productRequest, userId);
            return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Erreur lors de la création du produit: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue lors de la création du produit");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/my-products")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> getMyProducts(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentification requise");
        }
        try {
            List<ProductResponse> products = productService.getProductsByUserId(userId);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des produits: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue lors de la récupération des produits");
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody ProductRequest productRequest, 
            @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentification requise");
        }
        try {
            Optional<ProductResponse> updatedProduct = productService.updateProduct(id, productRequest, userId);
            if (updatedProduct.isPresent()) {
                return ResponseEntity.ok(updatedProduct.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du produit {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue lors de la mise à jour du produit");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> deleteProduct(
            @PathVariable String id, 
            @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentification requise");
        }
        boolean deleted = productService.deleteProduct(id, userId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
