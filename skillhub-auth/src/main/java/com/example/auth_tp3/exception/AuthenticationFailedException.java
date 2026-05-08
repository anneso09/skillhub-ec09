package com.example.auth_tp3.exception;

/**
 * Exception levée lors d'un échec d'authentification.
 *
 * <p>Lancée par {@link com.example.auth_tp3.service.AuthService#login}
 * dans deux cas : email introuvable en base ou mot de passe incorrect.
 * Le même message est renvoyé dans les deux cas pour éviter
 * l'énumération des comptes enregistrés.</p>
 *
 * <p>Interceptée par {@link GlobalExceptionHandler}
 * et transformée en réponse HTTP 401 Unauthorized.</p>
 *
 * @author Ton nom
 * @version 5.0
 */
public class AuthenticationFailedException extends RuntimeException {

    /**
     * Crée une exception d'authentification avec un message explicite.
     *
     * @param message Description de l'erreur d'authentification
     */
    public AuthenticationFailedException(String message) {
        super(message);
    }
}