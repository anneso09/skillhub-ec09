package com.example.auth_tp3;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Configuration CORS (Cross-Origin Resource Sharing) pour SkillHub Auth.
 *
 * <p>Autorise les requêtes cross-origin provenant de React et Laravel
 * vers Spring Boot. Sans cette configuration, le navigateur bloquerait
 * toutes les requêtes venant d'origines différentes (ports différents).</p>
 *
 * <p>Origines autorisées :
 * <ul>
 *   <li>{@code http://localhost:5173} — React en développement (Vite)</li>
 *   <li>{@code http://localhost:3000} — React via Docker</li>
 *   <li>{@code http://localhost:8000} — Laravel backend</li>
 *   <li>{@code http://localhost:80}   — nginx via Docker</li>
 * </ul>
 * </p>
 *
 * @author MU_202603
 * @version 5.0
 */
@Configuration
public class CorsConfig {

    /**
     * Crée et configure le filtre CORS appliqué à toutes les requêtes HTTP.
     *
     * <p><strong>⚠️ Important :</strong> {@code setAllowCredentials(true)} interdit
     * l'utilisation de {@code addAllowedOrigin("*")} — les origines doivent
     * être listées explicitement.</p>
     *
     * @return Le {@link CorsFilter} configuré et enregistré par Spring Boot
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedOrigin("http://127.0.0.1:5173");
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("http://localhost:8000");
        config.addAllowedOrigin("http://localhost:80");

        // Autorise toutes les méthodes HTTP (GET, POST, PUT, DELETE, OPTIONS)
        // OPTIONS est indispensable pour les preflight requests CORS
        config.addAllowedMethod("*");

        // Autorise tous les headers, notamment :
        // Authorization: Bearer <token> et Content-Type: application/json
        config.addAllowedHeader("*");

        // Autorise l'envoi du header Authorization (nécessaire pour JWT)
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}