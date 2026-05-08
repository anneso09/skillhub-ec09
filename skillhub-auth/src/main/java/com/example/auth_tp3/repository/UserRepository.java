package com.example.auth_tp3.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.auth_tp3.entity.User;

/**
 * Repository JPA pour l'accès aux données de la table "users".
 *
 * <p>Étend {@link JpaRepository} qui fournit automatiquement
 * les opérations CRUD de base sans écrire de SQL :
 * {@code save()}, {@code findById()}, {@code findAll()},
 * {@code delete()}, {@code count()}.</p>
 *
 * <p>Spring Data JPA génère automatiquement les requêtes SQL
 * à partir des noms des méthodes déclarées.</p>
 *
 * @author MU_202603
 * @version 5.0
 * @see com.example.auth_tp3.entity.User
 * @see com.example.auth_tp3.service.AuthService
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Recherche un utilisateur par son adresse email.
     *
     * <p>Génère automatiquement :
     * {@code SELECT * FROM users WHERE email = ? LIMIT 1}</p>
     *
     * @param email Adresse email à rechercher
     * @return Un {@link Optional} contenant l'utilisateur si trouvé,
     *         vide sinon
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifie si un utilisateur existe avec l'email donné.
     *
     * <p>Génère automatiquement :
     * {@code SELECT COUNT(*) FROM users WHERE email = ?}</p>
     *
     * <p>Utilisé dans {@link com.example.auth_tp3.service.AuthService#register}
     * pour bloquer les inscriptions avec un email déjà utilisé.</p>
     *
     * @param email Adresse email à vérifier
     * @return {@code true} si l'email existe déjà, {@code false} sinon
     */
    boolean existsByEmail(String email);
}