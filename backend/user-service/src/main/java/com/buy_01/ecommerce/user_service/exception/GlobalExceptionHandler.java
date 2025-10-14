package com.buy_01.ecommerce.user_service.exception;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.buy_01.ecommerce.user_service.model.User;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.buy_01.ecommerce.user_service.dto.ErrorResponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

/**
 * Gestionnaire global des exceptions pour l'application.
 * Capture et traite les exceptions non gérées et renvoie des réponses HTTP appropriées.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gère les erreurs de validation des champs annotés avec @Valid.
     *
     * @param ex l'exception de validation
     * @return une réponse HTTP 400 avec les détails des erreurs de validation
     */
    /**
     * Gère les erreurs de désérialisation des énumérations (comme les rôles invalides).
     *
     * @param ex l'exception de désérialisation
     * @return une réponse HTTP 400 avec un message d'erreur explicite
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String errorMessage = "Requête invalide";
        
        // Vérifie si l'erreur est liée à une énumération invalide
        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            String message = ex.getCause().getMessage();
            if (message.contains("not one of the values accepted for Enum class")) {
                errorMessage = "Valeur de rôle invalide. Les rôles valides sont : " + 
                    Arrays.stream(User.UserRole.values())
                        .map(Enum::name)
                        .collect(Collectors.joining(", "));
            }
        }
        
        return new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            errorMessage,
            LocalDateTime.now()
        );
    }

    /**
     * Gère les erreurs de validation des champs annotés avec @Valid.
     *
     * @param ex l'exception de validation
     * @return une réponse HTTP 400 avec les détails des erreurs de validation
     */
    /**
     * Gère les erreurs d'état non valide (comme promouvoir un administrateur existant)
     *
     * @param ex l'exception d'état non valide
     * @return une réponse HTTP 400 avec un message d'erreur explicite
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalStateException(IllegalStateException ex) {
        return new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
    }

    /**
     * Gère les erreurs de validation des champs annotés avec @Valid.
     *
     * @param ex l'exception de validation
     * @return une réponse HTTP 400 avec les détails des erreurs de validation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
            
        return new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Données invalides: " + errorMessage,
            LocalDateTime.now()
        );
    }
    
    /**
     * Gère les erreurs d'authentification (mauvais identifiants).
     *
     * @param ex l'exception d'authentification
     * @return une réponse HTTP 401 avec le message d'erreur
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleBadCredentialsException(BadCredentialsException ex) {
        return new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Email ou mot de passe incorrect",
            LocalDateTime.now()
        );
    }
    
    /**
     * Gère les erreurs d'authentification génériques.
     *
     * @param ex l'exception d'authentification
     * @return une réponse HTTP 401 avec le message d'erreur
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthenticationException(AuthenticationException ex) {
        return new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Échec de l'authentification",
            LocalDateTime.now()
        );
    }
    
    /**
     * Gère les erreurs de ressource non trouvée.
     *
     * @param ex l'exception de ressource non trouvée
     * @return une réponse HTTP 404 avec le message d'erreur
     */
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException ex) {
        return new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
    }
    
    /**
     * Gère les erreurs de conflit (comme un email déjà utilisé).
     *
     * @param ex l'exception de conflit
     * @return une réponse HTTP 409 avec le message d'erreur
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
    }
    
    /**
     * Gère les violations de contraintes de validation.
     *
     * @param ex l'exception de violation de contrainte
     * @return une réponse HTTP 400 avec les détails des violations
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(", "));
            
        return new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Erreur de validation: " + errorMessage,
            LocalDateTime.now()
        );
    }
    
    /**
     * Gestionnaire d'exception générique pour toutes les autres exceptions non gérées.
     *
     * @param ex l'exception non gérée
     * @return une réponse HTTP 500 avec un message d'erreur générique
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDeniedException(org.springframework.security.access.AccessDeniedException ex) {
        log.warn("Accès refusé: {}", ex.getMessage());
        String message = "Accès refusé. Vous n'avez pas les autorisations nécessaires pour effectuer cette action.";
        
        // Message plus détaillé si c'est une erreur de rôle
        if (ex.getMessage() != null && ex.getMessage().contains("Access is denied")) {
            message = "Accès refusé. Cette action nécessite des privilèges d'administrateur.";
        }
        
        return new ErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            message,
            LocalDateTime.now()
        );
    }

    /**
     * Gère les erreurs 404 pour les endpoints inexistants.
     * Cette méthode est appelée quand aucun handler n'est trouvé pour une requête.
     *
     * @param ex l'exception NoHandlerFoundException
     * @param request la requête web
     * @return une réponse HTTP 404 avec un message clair
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoHandlerFoundException(NoHandlerFoundException ex, WebRequest request) {
        String requestUri = request.getDescription(false).replace("uri=", "");
        log.warn("Endpoint non trouvé: {} {}", ex.getHttpMethod(), requestUri);
        
        return new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            String.format("L'endpoint '%s %s' n'existe pas", ex.getHttpMethod(), ex.getRequestURL()),
            LocalDateTime.now()
        );
    }

    /**
     * Gère les erreurs 404 pour les ressources statiques inexistantes (Spring 6.1+).
     *
     * @param ex l'exception NoResourceFoundException
     * @param request la requête web
     * @return une réponse HTTP 404 avec un message clair
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoResourceFoundException(NoResourceFoundException ex, WebRequest request) {
        String requestUri = request.getDescription(false).replace("uri=", "");
        log.warn("Ressource non trouvée: {}", requestUri);
        
        return new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            String.format("La ressource '%s' n'a pas été trouvée", ex.getResourcePath()),
            LocalDateTime.now()
        );
    }

    /**
     * Gère les erreurs de méthode HTTP non supportée (405).
     *
     * @param ex l'exception de méthode non supportée
     * @param request la requête web
     * @return une réponse HTTP 405 avec le message d'erreur approprié
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponse handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, 
            WebRequest request) {
        
        String requestUri = request.getDescription(false).replace("uri=", "");
        String[] supportedMethods = ex.getSupportedMethods();
        String supportedMethodsStr = supportedMethods != null 
                ? String.join(", ", supportedMethods)
                : "Aucune méthode supportée";
        
        String errorMessage = String.format(
            "La méthode HTTP '%s' n'est pas supportée pour cette URL. Méthodes supportées : %s",
            ex.getMethod(),
            supportedMethodsStr
        );
        
        log.warn("Méthode non supportée: {} {}", ex.getMethod(), requestUri);
        
        return new ErrorResponse(
            HttpStatus.METHOD_NOT_ALLOWED.value(),
            errorMessage,
            LocalDateTime.now()
        );
    }

    /**
     * Gestionnaire d'exception générique pour toutes les autres exceptions non gérées.
     *
     * @param ex l'exception non gérée
     * @param request la requête web
     * @return une réponse HTTP 500 avec un message d'erreur générique
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAllUncaughtException(Exception ex, WebRequest request) {
        String requestUri = request.getDescription(false).replace("uri=", "");
        log.error("Erreur inattendue sur {}: {}", requestUri, ex.getMessage(), ex);
        
        return new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Une erreur interne s'est produite. Veuillez réessayer plus tard.",
            LocalDateTime.now()
        );
    }

}
