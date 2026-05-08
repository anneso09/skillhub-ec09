package com.example.auth_tp3.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth_tp3.entity.User;
import com.example.auth_tp3.repository.UserRepository;
import com.example.auth_tp3.service.AuthService;
import com.example.auth_tp3.service.JwtService;

import io.jsonwebtoken.Claims;

/**
 * Contrôleur REST exposant les endpoints d'authentification de SkillHub.
 *
 * <p>3 routes exposées :
 * <ul>
 *   <li>{@code POST /api/auth/register} — inscription d'un nouvel utilisateur</li>
 *   <li>{@code POST /api/auth/login} — connexion et génération du token JWT</li>
 *   <li>{@code POST /api/auth/validate} — validation d'un token JWT
 *       appelée par le middleware Laravel</li>
 * </ul>
 * </p>
 *
 * <p>Ce controller ne contient aucune logique métier —
 * tout est délégué à {@link AuthService}.</p>
 *
 * @author Ton nom
 * @version 5.0
 * @see AuthService
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    /** Service contenant la logique métier d'authentification. */
    private final AuthService    authService;

    /** Repository d'accès aux données utilisateurs. */
    private final UserRepository userRepository;

    /** Service de génération et validation des tokens JWT. */
    private final JwtService     jwtService;

    /**
     * Constructeur avec injection des dépendances.
     *
     * @param authService    Service d'authentification
     * @param userRepository Repository JPA des utilisateurs
     * @param jwtService     Service de gestion des tokens JWT
     */
    public AuthController(AuthService authService,
                          UserRepository userRepository,
                          JwtService jwtService) {
        this.authService    = authService;
        this.userRepository = userRepository;
        this.jwtService     = jwtService;
    }

    /**
     * Inscrit un nouvel utilisateur sur la plateforme SkillHub.
     *
     * @param request Objet contenant nom, prenom, email, password, role
     * @return 201 CREATED avec message de confirmation et données de l'utilisateur
     * @throws Exception Si la validation échoue ou si l'email est déjà utilisé
     */
    @PostMapping("/auth/register")
    public ResponseEntity<Map<String, Object>> register(
            @RequestBody RegisterRequest request) throws Exception {

        User user = authService.register(
                request.getNom(),
                request.getPrenom(),
                request.getEmail(),
                request.getPassword(),
                request.getRole()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Inscription réussie");
        response.put("email",   user.getEmail());
        response.put("nom",     user.getNom());
        response.put("prenom",  user.getPrenom());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authentifie un utilisateur et retourne un token JWT.
     *
     * @param request Objet contenant email et password
     * @return 200 OK avec accessToken, role, nom, prenom, email
     * @throws Exception Si l'email est introuvable ou le mot de passe incorrect
     */
    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody LoginRequest request) throws Exception {

        String token = authService.login(
                request.getEmail(),
                request.getPassword()
        );

        User user = userRepository.findByEmail(request.getEmail())
                                  .orElseThrow();

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", token);
        response.put("role",        user.getRole());
        response.put("nom",         user.getNom());
        response.put("prenom",      user.getPrenom());
        response.put("email",       user.getEmail());

        return ResponseEntity.ok(response);
    }

    /**
     * Valide un token JWT et retourne les informations de l'utilisateur.
     *
     * <p>Cette route est appelée par le middleware Laravel
     * {@code JwtVerifyMiddleware.php} pour sécuriser chaque
     * requête entrante sur l'API Laravel.</p>
     *
     * @param body Map contenant le champ "token" à valider
     * @return 200 OK avec email, role, userId si token valide,
     *         ou 401 UNAUTHORIZED si token invalide ou expiré
     */
    @PostMapping("/auth/validate")
    public ResponseEntity<Map<String, Object>> validate(
            @RequestBody Map<String, String> body) {
        try {
            String token = body.get("token");
            Claims claims = jwtService.validateToken(token);

            Map<String, Object> response = new HashMap<>();
            response.put("email",  claims.getSubject());
            response.put("role",   claims.get("role",   String.class));
            response.put("userId", claims.get("userId", Long.class));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity
                    .status(401)
                    .body(Map.of("message", "Token invalide"));
        }
    }
}