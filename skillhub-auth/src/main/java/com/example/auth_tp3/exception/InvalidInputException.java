package com.example.auth_tp3.exception;

/**
 * Exception levée lorsque les données envoyées par le client sont invalides.
 *
 * <p>Lancée par {@link com.example.auth_tp3.service.AuthService#register}
 * dans ces cas : email null ou sans "@", mot de passe de moins de
 * 12 caractères, rôle différent de "apprenant" ou "formateur".</p>
 *
 * <p>Interceptée par {@link GlobalExceptionHandler}
 * et transformée en réponse HTTP 400 Bad Request.</p>
 *
 * @author Ton nom
 * @version 5.0
 */
public class InvalidInputException extends RuntimeException {

    /**
     * Crée une exception de données invalides avec un message explicite.
     *
     * @param message Description de la donnée invalide
     */
    public InvalidInputException(String message) {
        super(message);
    }
}