package com.example.auth_tp3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée principal de l'application Spring Boot SkillHub Auth.
 *
 * <p>Cette classe est exécutée en premier lors du démarrage via :
 * <pre>./mvnw spring-boot:run</pre>
 * ou via Docker lors du lancement du conteneur.</p>
 *
 * <p>{@link SpringBootApplication} regroupe 3 annotations :
 * <ul>
 *   <li>{@code @Configuration} — ce fichier peut définir des beans Spring</li>
 *   <li>{@code @EnableAutoConfiguration} — Spring configure automatiquement
 *       les composants détectés (JPA, Web...)</li>
 *   <li>{@code @ComponentScan} — Spring scanne tous les fichiers du package
 *       {@code com.example.auth_tp3} pour trouver les {@code @Service},
 *       {@code @Repository}, {@code @Controller}...</li>
 * </ul>
 * </p>
 *
 * @author MU_202603
 * @version 5.0
 */
@SpringBootApplication
public class AuthTp3Application {

    /**
     * Lance le serveur Spring Boot embarqué (Tomcat) sur le port 8080.
     *
     * @param args Arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthTp3Application.class, args);
    }
}