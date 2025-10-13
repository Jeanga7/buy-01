package com.buy_01.ecommerce.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception levée lorsqu'une erreur d'authentification se produit.
 * Génère une réponse HTTP 401 (Unauthorized) lorsqu'elle est attrapée par le gestionnaire d'exceptions.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserAuthenticationException extends RuntimeException {
    
    /**
     * Construit une nouvelle exception avec un message d'erreur spécifique.
     *
     * @param message le message d'erreur détaillant la raison de l'échec d'authentification
     */
    public UserAuthenticationException(String message) {
        super(message);
    }

    /**
     * Construit une nouvelle exception avec un message d'erreur et une cause sous-jacente.
     *
     * @param message le message d'erreur détaillant la raison de l'échec d'authentification
     * @param cause la cause sous-jacente de l'exception
     */
    public UserAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
