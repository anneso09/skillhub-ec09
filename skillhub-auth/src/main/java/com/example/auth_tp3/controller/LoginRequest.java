package com.example.auth_tp3.controller;

import lombok.Data;

/**
 * DTO représentant le corps de la requête POST /api/auth/login.
 *
 * <p>Transporte les données de connexion depuis le client
 * vers {@link AuthController}, sans aucune logique métier.</p>
 *
 * <p>Exemple de payload JSON attendu :
 * <pre>
 * {
 *   "email":    "alice@mail.com",
 *   "password": "MotDePasse123!"
 * }
 * </pre>
 * </p>
 *
 * @author MU_202603
 * @version 5.0
 */
@Data
public class LoginRequest {

    /** Adresse email de l'utilisateur (identifiant de connexion). */
    private String email;

    /** Mot de passe en clair saisi par l'utilisateur. */
    private String password;
}