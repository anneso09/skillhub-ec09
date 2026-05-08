package com.example.auth_tp3.controller;

import lombok.Data;

/**
 * DTO représentant le corps de la requête PUT /api/auth/change-password.
 *
 * <p>Exemple de payload JSON attendu :
 * <pre>
 * {
 *   "email":           "alice@mail.com",
 *   "oldPassword":     "AncienMotDePasse123!",
 *   "newPassword":     "NouveauMotDePasse123!",
 *   "confirmPassword": "NouveauMotDePasse123!"
 * }
 * </pre>
 * </p>
 *
 * @author MU_202603
 * @version 5.0
 */
@Data
public class ChangePasswordRequest {

    /** Adresse email de l'utilisateur souhaitant changer son mot de passe. */
    private String email;

    /** Ancien mot de passe en clair pour vérification. */
    private String oldPassword;

    /** Nouveau mot de passe en clair (minimum 12 caractères). */
    private String newPassword;

    /** Confirmation du nouveau mot de passe — doit être identique à newPassword. */
    private String confirmPassword;
}