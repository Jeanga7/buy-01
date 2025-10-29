package com.buy_01.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            // Route pour l'authentification
            .route("auth-service", r -> r.path("/api/auth/**")
                .filters(f -> f.rewritePath("/api/auth/(?<segment>.*)", "/api/auth/${segment}"))
                .uri("lb://user-service"))
                
            // Route pour les utilisateurs
            .route("user-service", r -> r.path("/api/users/**")
                .filters(f -> f.rewritePath("/api/users/(?<segment>.*)", "/api/users/${segment}"))
                .uri("lb://user-service"))
                
            // Route pour les produits
            .route("product-service", r -> r.path("/api/products/**")
                .filters(f -> f.rewritePath("/api/products/(?<segment>.*)", "/api/products/${segment}"))
                .uri("lb://product-service"))
            .build();
    }
}
