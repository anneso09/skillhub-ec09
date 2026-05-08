package com.example.auth_tp3.service;

import org.springframework.stereotype.Service;

import com.example.auth_tp3.entity.User;
import com.example.auth_tp3.exception.AuthenticationFailedException;
import com.example.auth_tp3.exception.InvalidInputException;
import com.example.auth_tp3.exception.ResourceConflictException;
import com.example.auth_tp3.repository.UserRepository;

/**
 * Service principal de l'authentification pour SkillHub.
 *
 * <p>
 * Contient toute la logique métier d'authentification : validation des données,
 * vérification de l'unicité de l'email, chiffrement du mot de passe,
 * vérification au login, et génération du token JWT.</p>
 *
 * * <p>
 * {@link com.example.auth_tp3.controller.AuthController} délègue... tout le
 * traitement à cette classe — le controller ne fait que recevoir la requête
 * HTTP et renvoyer la réponse.</p>
 *
 * <p>
 * <strong>⚠️ Cette implémentation utilise un chiffrement réversible (AES via
 * Master Key) nécessaire au protocole HMAC. Ne pas utiliser telle quelle en
 * production sans audit de sécurité préalable.</strong></p>
 *
 * @author Ton nom
 * @version 5.0
 * @see com.example.auth_tp3.controller.AuthController
 * @see EncryptionService
 * @see JwtService
 */
@Service
public class AuthService {

    /**
     * Repository d'accès aux données utilisateurs.
     */
    private final UserRepository userRepository;

    /**
     * Service de chiffrement AES des mots de passe.
     */
    private final EncryptionService encryptionService;

    /**
     * Service de génération et validation des tokens JWT.
     */
    private final JwtService jwtService;

    /**
     * Constructeur avec injection des dépendances.
     *
     * @param userRepository Repository JPA des utilisateurs
     * @param encryptionService Service de chiffrement AES
     * @param jwtService Service de gestion des tokens JWT
     */
    public AuthService(UserRepository userRepository,
            EncryptionService encryptionService,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
    }

    /**
     * Inscrit un nouvel utilisateur sur la plateforme SkillHub.
     *
     * <p>
     * Étapes exécutées dans l'ordre :
     * <ol>
     * <li>Validation du format de l'email</li>
     * <li>Validation de la longueur du mot de passe (min. 12 caractères)</li>
     * <li>Vérification que l'email n'est pas déjà utilisé</li>
     * <li>Validation du rôle (apprenant ou formateur)</li>
     * <li>Chiffrement du mot de passe via {@link EncryptionService}</li>
     * <li>Sauvegarde de l'utilisateur en base</li>
     * </ol>
     * </p>
     *
     * @param nom Nom de famille de l'utilisateur
     * @param prenom Prénom de l'utilisateur
     * @param email Adresse email (identifiant unique)
     * @param password Mot de passe en clair (minimum 12 caractères)
     * @param role Rôle souhaité : "apprenant" ou "formateur"
     * @return L'entité {@link User} persistée avec son ID généré
     * @throws InvalidInputException Si email, mot de passe ou rôle invalide
     * @throws ResourceConflictException Si l'email est déjà utilisé
     * @throws Exception Si le chiffrement échoue
     */
    public User register(String nom, String prenom,
            String email, String password,
            String role) throws Exception {

        if (email == null || !email.contains("@")) {
            throw new InvalidInputException("Format d'email invalide");
        }

        if (password == null || password.length() < 12) {
            throw new InvalidInputException(
                    "Le mot de passe doit faire au moins 12 caractères"
            );
        }

        if (userRepository.existsByEmail(email)) {
            throw new ResourceConflictException("Cet email est déjà utilisé");
        }

        if (role == null
                || (!role.equals("apprenant") && !role.equals("formateur"))) {
            throw new InvalidInputException(
                    "Le rôle doit être apprenant ou formateur"
            );
        }

        String encryptedPassword = encryptionService.encrypt(password);

        User user = new User();
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        user.setPassword(encryptedPassword);
        user.setRole(role);

        return userRepository.save(user);
    }

    /**
     * Authentifie un utilisateur et génère un token JWT.
     *
     * <p>
     * Étapes exécutées dans l'ordre :
     * <ol>
     * <li>Recherche de l'utilisateur par email</li>
     * <li>Déchiffrement et comparaison du mot de passe</li>
     * <li>Génération du token JWT signé</li>
     * </ol>
     * </p>
     *
     * <p>
     * <strong>Sécurité :</strong> le même message d'erreur est renvoyé que
     * l'email soit inconnu ou que le mot de passe soit incorrect, afin
     * d'empêcher l'énumération des comptes.</p>
     *
     * @param email Adresse email de l'utilisateur
     * @param password Mot de passe en clair saisi par l'utilisateur
     * @return Le token JWT signé à retourner au client
     * @throws AuthenticationFailedException Si email introuvable ou mot de
     * passe incorrect
     * @throws Exception Si le déchiffrement échoue
     */
    public String login(String email, String password) throws Exception {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationFailedException(
                "Email ou mot de passe incorrect"
        ));

        String storedPassword = encryptionService.decrypt(user.getPassword());
        if (!storedPassword.equals(password)) {
            throw new AuthenticationFailedException(
                    "Email ou mot de passe incorrect"
            );
        }

        return jwtService.generateToken(user);
    }
}
