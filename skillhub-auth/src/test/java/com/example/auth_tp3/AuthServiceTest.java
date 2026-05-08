package com.example.auth_tp3;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.auth_tp3.entity.User;
import com.example.auth_tp3.exception.AuthenticationFailedException;
import com.example.auth_tp3.exception.InvalidInputException;
import com.example.auth_tp3.exception.ResourceConflictException;
import com.example.auth_tp3.repository.UserRepository;
import com.example.auth_tp3.service.AuthService;
import com.example.auth_tp3.service.EncryptionService;
import com.example.auth_tp3.service.JwtService;

/**
 * Tests unitaires du service d'authentification {@link AuthService}.
 *
 * <p>Vérifie la logique métier de l'inscription et de la connexion :
 * validations, gestion des erreurs, chiffrement et génération JWT.</p>
 *
 * <p>Utilise Mockito pour simuler les dépendances
 * ({@link UserRepository}, {@link EncryptionService}, {@link JwtService})
 * afin de tester la logique de {@link AuthService} de manière isolée.</p>
 *
 * @author Ton nom
 * @version 5.0
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    /** Mock du repository JPA des utilisateurs. */
    @Mock
    private UserRepository userRepository;

    /** Mock du service de chiffrement AES. */
    @Mock
    private EncryptionService encryptionService;

    /** Mock du service de génération des tokens JWT. */
    @Mock
    private JwtService jwtService;

    /** Instance du service testé avec les mocks injectés. */
    @InjectMocks
    private AuthService authService;

    /** Utilisateur de test réutilisé dans plusieurs tests. */
    private User testUser;

    /**
     * Initialise l'utilisateur de test avant chaque méthode de test.
     *
     * @throws Exception Si l'initialisation échoue
     */
    @BeforeEach
    void setUp() throws Exception {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword("encrypted_password");
        testUser.setNom("Dupont");
        testUser.setPrenom("Jean");
        testUser.setRole("apprenant");
    }

    // ─────────────────────────────────────────────────────────
    // TESTS REGISTER
    // ─────────────────────────────────────────────────────────

    /**
     * Vérifie qu'une inscription valide crée et sauvegarde l'utilisateur.
     *
     * @throws Exception Si l'inscription échoue de manière inattendue
     */
    @Test
    void register_succes() throws Exception {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(encryptionService.encrypt(any())).thenReturn("encrypted");
        when(userRepository.save(any())).thenReturn(testUser);

        User result = authService.register(
            "Dupont", "Jean", "test@example.com",
            "MotDePasse123!", "apprenant"
        );

        assertNotNull(result);
        verify(userRepository, times(1)).save(any());
    }

    /**
     * Vérifie qu'une inscription avec un email déjà utilisé
     * lève une {@link ResourceConflictException}.
     */
    @Test
    void register_emailDejaUtilise() {
        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThrows(ResourceConflictException.class, () ->
            authService.register(
                "Dupont", "Jean", "test@example.com",
                "MotDePasse123!", "apprenant"
            )
        );
    }

    /**
     * Vérifie qu'un email sans arobase lève une {@link InvalidInputException}.
     */
    @Test
    void register_emailInvalide() {
        assertThrows(InvalidInputException.class, () ->
            authService.register(
                "Dupont", "Jean", "emailsansarobase",
                "MotDePasse123!", "apprenant"
            )
        );
    }

    /**
     * Vérifie qu'un mot de passe de moins de 12 caractères
     * lève une {@link InvalidInputException}.
     */
    @Test
    void register_motDePasseTropCourt() {
        assertThrows(InvalidInputException.class, () ->
            authService.register(
                "Dupont", "Jean", "test@example.com",
                "court", "apprenant"
            )
        );
    }

    /**
     * Vérifie qu'un rôle invalide (ni "apprenant" ni "formateur")
     * lève une {@link InvalidInputException}.
     */
    @Test
    void register_roleInvalide() {
        assertThrows(InvalidInputException.class, () ->
            authService.register(
                "Dupont", "Jean", "test@example.com",
                "MotDePasse123!", "admin"
            )
        );
    }

    // ─────────────────────────────────────────────────────────
    // TESTS LOGIN
    // ─────────────────────────────────────────────────────────

    /**
     * Vérifie qu'un login valide retourne un token JWT.
     *
     * @throws Exception Si le login échoue de manière inattendue
     */
    @Test
    void login_succes() throws Exception {
        when(userRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(testUser));
        when(encryptionService.decrypt("encrypted_password"))
            .thenReturn("MotDePasse123!");
        when(jwtService.generateToken(testUser))
            .thenReturn("fake_jwt_token");

        String token = authService.login("test@example.com", "MotDePasse123!");

        assertEquals("fake_jwt_token", token);
    }

    /**
     * Vérifie qu'un login avec un email inexistant
     * lève une {@link AuthenticationFailedException}.
     */
    @Test
    void login_emailInexistant() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(AuthenticationFailedException.class, () ->
            authService.login("inconnu@example.com", "MotDePasse123!")
        );
    }

    /**
     * Vérifie qu'un login avec un mauvais mot de passe
     * lève une {@link AuthenticationFailedException}.
     *
     * @throws Exception Si le déchiffrement échoue de manière inattendue
     */
    @Test
    void login_mauvaisMotDePasse() throws Exception {
        when(userRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(testUser));
        when(encryptionService.decrypt("encrypted_password"))
            .thenReturn("MotDePasse123!");

        assertThrows(AuthenticationFailedException.class, () ->
            authService.login("test@example.com", "MauvaisMotDePasse!")
        );
    }
}