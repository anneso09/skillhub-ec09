package com.example.auth_tp3.exception;

/**
 * Exception levée lors d'un conflit de ressource en base de données.
 *
 * <p>Lancée par {@link com.example.auth_tp3.service.AuthService#register}
 * lorsqu'un utilisateur tente de s'inscrire avec un email
 * déjà existant en base de données.</p>
 *
 * <p>Interceptée par {@link GlobalExceptionHandler}
 * et transformée en réponse HTTP 409 Conflict.</p>
 *
 * @author Ton nom
 * @version 5.0
 */
public class ResourceConflictException extends RuntimeException {

    /**
     * Crée une exception de conflit de ressource avec un message explicite.
     *
     * @param message Description du conflit détecté
     */
    public ResourceConflictException(String message) {
        super(message);
    }
}