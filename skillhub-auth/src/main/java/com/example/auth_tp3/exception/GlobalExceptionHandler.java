package com.example.auth_tp3.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Gestionnaire global des exceptions de l'application SkillHub.
 *
 * <p>Intercepte toutes les exceptions métier et les transforme
 * en réponses JSON cohérentes avec le format suivant :
 * <pre>
 * {
 *   "timestamp" : "2026-05-06T10:30:00",
 *   "status"    : 401,
 *   "error"     : "Unauthorized",
 *   "message"   : "Email ou mot de passe incorrect",
 *   "path"      : "/api/auth/login"
 * }
 * </pre>
 * </p>
 *
 * @author Ton nom
 * @version 5.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Construit une réponse d'erreur JSON uniforme.
     *
     * @param status  Code HTTP de l'erreur
     * @param message Message d'erreur personnalisé
     * @param request Requête HTTP ayant déclenché l'erreur
     * @return Map contenant timestamp, status, error, message, path
     */
    private Map<String, Object> buildError(HttpStatus status,
                                           String message,
                                           HttpServletRequest request) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status",    status.value());
        error.put("error",     status.getReasonPhrase());
        error.put("message",   message);
        error.put("path",      request.getRequestURI());
        return error;
    }

    /**
     * Gère les erreurs de données invalides (400 Bad Request).
     *
     * @param ex      Exception contenant le message d'erreur
     * @param request Requête HTTP ayant déclenché l'erreur
     * @return 400 BAD REQUEST avec détails de l'erreur en JSON
     */
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidInput(
            InvalidInputException ex,
            HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
    }

    /**
     * Gère les échecs d'authentification (401 Unauthorized).
     *
     * @param ex      Exception contenant le message d'erreur
     * @param request Requête HTTP ayant déclenché l'erreur
     * @return 401 UNAUTHORIZED avec détails de l'erreur en JSON
     */
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<Map<String, Object>> handleAuthFailed(
            AuthenticationFailedException ex,
            HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(buildError(HttpStatus.UNAUTHORIZED, ex.getMessage(), request));
    }

    /**
     * Gère les conflits de ressources en base de données (409 Conflict).
     *
     * @param ex      Exception contenant le message d'erreur
     * @param request Requête HTTP ayant déclenché l'erreur
     * @return 409 CONFLICT avec détails de l'erreur en JSON
     */
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(
            ResourceConflictException ex,
            HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(buildError(HttpStatus.CONFLICT, ex.getMessage(), request));
    }
}