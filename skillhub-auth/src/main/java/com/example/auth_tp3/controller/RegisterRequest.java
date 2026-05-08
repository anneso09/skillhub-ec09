package com.example.auth_tp3.controller;

import lombok.Data;

/**
 * DTO représentant le corps de la requête POST /api/auth/register.
 *
 * <p>Transporte les données du formulaire d'inscription depuis
 * le client vers {@link AuthController}, sans logique métier.</p>
 *
 * <p>Exemple de payload JSON attendu :
 * <pre>
 * {
 *   "nom":      "Martin",
 *   "prenom":   "Sophie",
 *   "email":    "sophie@test.com",
 *   "password": "MotDePasse123!",
 *   "role":     "apprenant"
 * }
 * </pre>
 * </p>
 *
 * @author MU_202603
 * @version 5.0
 */
@Data
public class RegisterRequest {

    /** Nom de famille de l'utilisateur. */
    private String nom;

    /** Prénom de l'utilisateur. */
    private String prenom;

    /** Adresse email unique utilisée comme identifiant de connexion. */
    private String email;

    /** Mot de passe en clair (minimum 12 caractères). */
    private String password;

    /**
     * Rôle de l'utilisateur sur la plateforme SkillHub.
     * Valeurs acceptées : "apprenant" ou "formateur".
     */
    private String role;
}