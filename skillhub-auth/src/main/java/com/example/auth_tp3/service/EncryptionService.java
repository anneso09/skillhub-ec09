package com.example.auth_tp3.service;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service de chiffrement et déchiffrement des mots de passe via AES-GCM.
 *
 * <p>Utilise AES-GCM (et non BCrypt) car le protocole HMAC du TP3
 * nécessite de retrouver le mot de passe original pour recalculer
 * la signature côté serveur.</p>
 *
 * <p>Format de stockage en base de données : {@code v1:Base64(iv):Base64(ciphertext)}</p>
 *
 * <p><strong>⚠️ La Master Key (APP_MASTER_KEY) ne doit jamais être
 * dans le code ou commitée sur Git — uniquement via variable d'environnement.</strong></p>
 *
 * @author MU_202603
 * @version 5.0
 */
@Service
public class EncryptionService {

    /** Taille du vecteur d'initialisation en bytes (recommandé pour AES-GCM). */
    private static final int GCM_IV_LENGTH  = 12;

    /** Taille du tag d'authentification GCM en bits (intégrité maximale). */
    private static final int GCM_TAG_LENGTH = 128;

    /** Clé AES-256 dérivée depuis APP_MASTER_KEY. */
    private final SecretKey masterKey;

    /** Générateur de nombres aléatoires cryptographiquement sûr. */
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Initialise le service en dérivant la clé AES depuis APP_MASTER_KEY.
     *
     * <p>Si la clé est absente ou vide, l'application refuse de démarrer.</p>
     *
     * @param masterKeyValue Valeur de APP_MASTER_KEY injectée depuis application.properties
     * @throws IllegalStateException Si APP_MASTER_KEY est absente ou vide
     */
    public EncryptionService(@Value("${app.master.key}") String masterKeyValue) {
        if (masterKeyValue == null || masterKeyValue.isBlank()) {
            throw new IllegalStateException(
                "APP_MASTER_KEY est absente ! " +
                "L'application ne peut pas démarrer sans la Master Key."
            );
        }
        byte[] keyBytes = masterKeyValue.getBytes();
        byte[] aesKey   = new byte[32];
        System.arraycopy(keyBytes, 0, aesKey, 0, Math.min(keyBytes.length, 32));
        this.masterKey  = new SecretKeySpec(aesKey, "AES");
    }

    /**
     * Chiffre un mot de passe en clair avec AES-GCM.
     *
     * <p>Un IV aléatoire est généré à chaque appel — deux chiffrements
     * du même mot de passe produisent des résultats différents,
     * ce qui protège contre les attaques par rainbow tables.</p>
     *
     * @param plaintext Mot de passe en clair à chiffrer
     * @return Chaîne chiffrée au format {@code v1:Base64(iv):Base64(ciphertext)}
     * @throws Exception Si le chiffrement échoue
     */
    public String encrypt(String plaintext) throws Exception {
        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, masterKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

        byte[] ciphertext       = cipher.doFinal(plaintext.getBytes());
        String ivBase64         = Base64.getEncoder().encodeToString(iv);
        String ciphertextBase64 = Base64.getEncoder().encodeToString(ciphertext);

        return "v1:" + ivBase64 + ":" + ciphertextBase64;
    }

    /**
     * Déchiffre un mot de passe chiffré avec AES-GCM.
     *
     * <p>GCM vérifie automatiquement l'intégrité des données —
     * si le ciphertext a été modifié en base, le déchiffrement
     * échoue avec une exception.</p>
     *
     * @param encryptedData Chaîne chiffrée au format {@code v1:Base64(iv):Base64(ciphertext)}
     * @return Mot de passe en clair
     * @throws Exception Si le déchiffrement échoue ou si le format est invalide
     */
    public String decrypt(String encryptedData) throws Exception {
        String[] parts = encryptedData.split(":");
        if (parts.length != 3 || !parts[0].equals("v1")) {
            throw new IllegalArgumentException("Format de données chiffrées invalide");
        }

        byte[] iv         = Base64.getDecoder().decode(parts[1]);
        byte[] ciphertext = Base64.getDecoder().decode(parts[2]);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, masterKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

        byte[] plaintext = cipher.doFinal(ciphertext);
        return new String(plaintext);
    }
}