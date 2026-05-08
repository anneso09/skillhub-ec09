package com.example.auth_tp3.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entité JPA représentant la table "users" en base de données.
 *
 * <p>Cette table est partagée entre Spring Boot et Laravel :
 * Spring Boot gère l'authentification (register, login),
 * Laravel gère la logique métier (formations, inscriptions).</p>
 *
 * <p><strong>⚠️ Cette implémentation utilise un chiffrement
 * réversible (AES via Master Key) nécessaire au protocole HMAC.
 * Ne pas utiliser telle quelle en production sans audit
 * de sécurité préalable.</strong></p>
 *
 * @author Ton nom
 * @version 5.0
 */
@Data
@Entity
@Table(name = "users")
public class User {

    /**
     * Identifiant unique auto-incrémenté (clé primaire).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom de famille de l'utilisateur. Ne peut pas être null.
     */
    @Column(nullable = false)
    private String nom;

    /**
     * Prénom de l'utilisateur. Ne peut pas être null.
     */
    @Column(nullable = false)
    private String prenom;

    /**
     * Adresse email unique de l'utilisateur.
     * Utilisée comme identifiant de connexion.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Mot de passe chiffré via AES + Master Key.
     * Jamais stocké en clair.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Rôle de l'utilisateur sur la plateforme SkillHub.
     * Valeurs acceptées : "apprenant" ou "formateur".
     */
    @Column(nullable = false)
    private String role;

    /**
     * Date et heure de création de l'enregistrement.
     * Initialisée automatiquement avant le premier INSERT.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Date et heure de la dernière modification.
     * Mise à jour automatiquement avant chaque UPDATE.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Initialise les timestamps avant le premier INSERT en base.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Met à jour le timestamp de modification avant chaque UPDATE.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}