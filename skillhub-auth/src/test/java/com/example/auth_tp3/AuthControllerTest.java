package com.example.auth_tp3;

import com.example.auth_tp3.controller.AuthController;
import com.example.auth_tp3.entity.User;
import com.example.auth_tp3.exception.ResourceConflictException;
import com.example.auth_tp3.repository.UserRepository;
import com.example.auth_tp3.service.AuthService;
import com.example.auth_tp3.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration du contrôleur REST {@link AuthController}.
 *
 * <p>Vérifie que les endpoints HTTP renvoient les bons codes de statut
 * et les bonnes structures JSON en fonction des réponses du service.</p>
 *
 * <p>Utilise {@link MockMvc} pour simuler les requêtes HTTP
 * sans démarrer un serveur réel, et {@link MockBean} pour
 * simuler les dépendances du controller.</p>
 *
 * @author Ton nom
 * @version 5.0
 */
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    /** Client HTTP simulé pour tester les endpoints REST. */
    @Autowired
    private MockMvc mockMvc;

    /** Mock du service d'authentification. */
    @MockBean
    private AuthService authService;

    /** Mock du repository utilisateurs — requis par AuthController. */
    @MockBean
    private UserRepository userRepository;

    /** Mock du service JWT — requis par AuthController. */
    @MockBean
    private JwtService jwtService;

    /**
     * Vérifie qu'une inscription valide retourne HTTP 201 Created
     * avec un message de confirmation.
     *
     * @throws Exception Si la requête MockMvc échoue
     */
    @Test
    void register_retourne201() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setNom("Dupont");
        user.setPrenom("Jean");
        user.setRole("apprenant");

        when(authService.register(any(), any(), any(), any(), any()))
            .thenReturn(user);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "nom": "Dupont",
                        "prenom": "Jean",
                        "email": "test@example.com",
                        "password": "MotDePasse123!",
                        "role": "apprenant"
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Inscription réussie"));
    }

    /**
     * Vérifie qu'un login valide retourne HTTP 200 OK
     * avec un token JWT dans la réponse.
     *
     * @throws Exception Si la requête MockMvc échoue
     */
    @Test
    void login_retourne200AvecToken() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setNom("Dupont");
        user.setPrenom("Jean");
        user.setRole("apprenant");

        when(authService.login(any(), any())).thenReturn("fake_jwt_token");
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "test@example.com",
                        "password": "MotDePasse123!"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("fake_jwt_token"));
    }

    /**
     * Vérifie qu'une inscription avec un email déjà utilisé
     * retourne HTTP 409 Conflict.
     *
     * @throws Exception Si la requête MockMvc échoue
     */
    @Test
    void register_emailDejaUtilise_retourne409() throws Exception {
        when(authService.register(any(), any(), any(), any(), any()))
            .thenThrow(new ResourceConflictException("Email déjà utilisé"));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "nom": "Dupont",
                        "prenom": "Jean",
                        "email": "test@example.com",
                        "password": "MotDePasse123!",
                        "role": "apprenant"
                    }
                    """))
                .andExpect(status().isConflict());
    }
}