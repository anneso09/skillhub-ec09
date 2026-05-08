package com.example.auth_tp3.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.auth_tp3.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * Service de génération et validation des tokens JWT pour SkillHub.
 *
 * <p>Un token JWT est composé de 3 parties : {@code header.payload.signature}
 * <ul>
 *   <li>header : algorithme utilisé (HS256)</li>
 *   <li>payload : données encodées (email, role, nom, prenom, userId)</li>
 *   <li>signature : garantit que le token n'a pas été modifié</li>
 * </ul>
 * </p>
 *
 * <p>Seul Spring Boot connaît le {@code jwt.secret} — Laravel et React
 * ne peuvent pas créer de tokens, seulement les utiliser.</p>
 *
 * @author MU_202603
 * @version 5.0
 */
@Service
public class JwtService {

    /** Clé secrète HMAC-SHA256 injectée depuis application.properties. */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /** Durée de validité du token : 24 heures en millisecondes. */
    private static final long EXPIRATION_MS = 86_400_000;

    /**
     * Génère un token JWT signé pour un utilisateur authentifié.
     *
     * <p>Le payload du token contient :
     * {@code sub} (email), {@code role}, {@code nom},
     * {@code prenom}, {@code userId}, {@code iat}, {@code exp}.</p>
     *
     * @param user Utilisateur authentifié dont les données seront encodées
     * @return Token JWT signé au format {@code header.payload.signature}
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role",   user.getRole());
        claims.put("nom",    user.getNom());
        claims.put("prenom", user.getPrenom());
        claims.put("userId", user.getId());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valide un token JWT et retourne son payload décodé.
     *
     * <p>Lève une exception si le token est invalide, expiré ou malformé.</p>
     *
     * @param token Token JWT à valider
     * @return Les {@link Claims} (payload décodé) si le token est valide
     * @throws io.jsonwebtoken.JwtException Si le token est invalide, expiré ou falsifié
     */
    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Convertit le secret JWT en clé cryptographique HMAC-SHA256.
     *
     * @return Clé de signature utilisable par l'algorithme HS256
     */
    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}