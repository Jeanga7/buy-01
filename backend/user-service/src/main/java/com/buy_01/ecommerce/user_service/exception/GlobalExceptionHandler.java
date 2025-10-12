package com.buy_01.ecommerce.user_service.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Gestionnaire global des exceptions pour l'application.
 * Capture et traite les exceptions non gérées et renvoie des réponses HTTP appropriées.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Constantes pour les messages d'erreur
    private static final String ERROR_MESSAGE_KEY = "message";
    private static final String UNEXPECTED_ERROR_MESSAGE = "Une erreur inattendue s'est produite";

    /**
     * Gère les erreurs de validation des champs annotés avec @Valid.
     *
     * @param ex l'exception de validation
     * @return une réponse HTTP 400 avec les détails des erreurs de validation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Erreur de validation des champs: {}", ex.getMessage());
        
        Map<String, String> errors = ex.getBindingResult().getAllErrors().stream()
                .filter(FieldError.class::isInstance)
                .map(FieldError.class::cast)
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), ""),
                        (existing, replacement) -> existing
                ));

        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Gère les violations de contraintes de validation.
     *
     * @param ex l'exception de violation de contrainte
     * @return une réponse HTTP 400 avec les détails des violations
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Violation de contrainte: {}", ex.getMessage());
        
        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (existing, replacement) -> existing
                ));

        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Gère les tentatives de création d'un utilisateur avec un email existant.
     *
     * @param ex l'exception d'utilisateur existant
     * @return une réponse HTTP 409 (Conflict) avec un message d'erreur
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        log.warn("Tentative de création d'un utilisateur existant: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    /**
     * Gère les tentatives d'accès à un utilisateur inexistant.
     *
     * @param ex l'exception d'utilisateur non trouvé
     * @return une réponse HTTP 404 (Not Found) avec un message d'erreur
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UserNotFoundException ex) {
        log.warn("Utilisateur non trouvé: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Gère toutes les autres exceptions non gérées.
     *
     * @param ex l'exception générique
     * @return une réponse HTTP 500 (Internal Server Error) avec un message générique
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        log.error("Erreur inattendue: {}", ex.getMessage(), ex);
        return buildErrorResponse(UNEXPECTED_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Construit une réponse d'erreur standardisée.
     *
     * @param message le message d'erreur
     * @param status  le statut HTTP de la réponse
     * @return une réponse HTTP avec le message d'erreur
     */
    private ResponseEntity<Map<String, String>> buildErrorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(Map.of(ERROR_MESSAGE_KEY, message));
    }
}
