package com.example.auth_tp3;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test de démarrage du contexte Spring Boot de l'application SkillHub Auth.
 *
 * <p>Vérifie que l'ensemble du contexte Spring se charge correctement
 * au démarrage — tous les beans sont bien créés et injectés.</p>
 *
 * @author Ton nom
 * @version 5.0
 */
@SpringBootTest
@ActiveProfiles("test")
class AuthTp3ApplicationTests {

    /**
     * Vérifie que le contexte Spring Boot démarre sans erreur.
     */
    @Test
    void contextLoads() {
    }
}